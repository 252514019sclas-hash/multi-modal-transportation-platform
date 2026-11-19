package com.simats.multimodaltransportationrecommendation.ui.map

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import com.simats.multimodaltransportationrecommendation.ui.theme.*

data class MapMarkerItem(
    val id: String = "",
    val title: String,
    val snippet: String? = null,
    val latitude: Double,
    val longitude: Double,
    val isUser: Boolean = false,
    val isDestination: Boolean = false,
    val isMeetingPoint: Boolean = false,
    val isTouristPlace: Boolean = false,
    val iconColorHue: Float = BitmapDescriptorFactory.HUE_AZURE,
    val extraData: Any? = null
)

@Composable
fun MoveSmartGoogleMapView(
    modifier: Modifier = Modifier,
    originLocation: LatLng? = null,
    userLocation: LatLng? = null,
    destinationLocation: LatLng? = null,
    routeWaypoints: List<LatLng> = emptyList(),
    additionalMarkers: List<MapMarkerItem> = emptyList(),
    cameraFocusTarget: LatLng? = null,
    triggerFitAll: Long = 0L,
    triggerRecenter: Long = 0L,
    zoomLevel: Float = 14.5f,
    onRecenterClick: () -> Unit = {},
    onMarkerClick: (MapMarkerItem) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var googleMapInstance by remember { mutableStateOf<GoogleMap?>(null) }
    var isMapAvailable by remember { mutableStateOf(true) }
    var mapInitError by remember { mutableStateOf<String?>(null) }

    val mapView = remember {
        MapView(context).apply {
            try {
                onCreate(Bundle())
            } catch (e: Exception) {
                isMapAvailable = false
                mapInitError = e.message
            }
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            try {
                when (event) {
                    Lifecycle.Event.ON_START -> mapView.onStart()
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    Lifecycle.Event.ON_STOP -> mapView.onStop()
                    Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                    else -> {}
                }
            } catch (_: Exception) {}
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            try {
                mapView.onDestroy()
            } catch (_: Exception) {}
        }
    }

    // Update map elements when state or coordinates change
    LaunchedEffect(googleMapInstance, originLocation, userLocation, destinationLocation, routeWaypoints, additionalMarkers) {
        val map = googleMapInstance ?: return@LaunchedEffect
        try {
            map.clear()

            val boundsBuilder = LatLngBounds.Builder()
            var hasPoints = false

            // 1. Origin Starting Point Marker
            originLocation?.let { oLoc ->
                map.addMarker(
                    MarkerOptions()
                        .position(oLoc)
                        .title("Starting Point (Origin)")
                        .snippet(String.format("Lat: %.4f, Lng: %.4f", oLoc.latitude, oLoc.longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                )
                boundsBuilder.include(oLoc)
                hasPoints = true
            }

            // 2. User Current GPS Location Marker
            userLocation?.let { uLoc ->
                map.addMarker(
                    MarkerOptions()
                        .position(uLoc)
                        .title("Your Current GPS Location")
                        .snippet(String.format("Lat: %.4f, Lng: %.4f", uLoc.latitude, uLoc.longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )
                boundsBuilder.include(uLoc)
                hasPoints = true
            }

            // 3. Destination Marker
            destinationLocation?.let { dLoc ->
                map.addMarker(
                    MarkerOptions()
                        .position(dLoc)
                        .title("Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
                boundsBuilder.include(dLoc)
                hasPoints = true
            }

            // 4. Additional Markers (e.g. Group Members, En-Route Stations, Attractions)
            additionalMarkers.forEach { mItem ->
                val pos = LatLng(mItem.latitude, mItem.longitude)
                map.addMarker(
                    MarkerOptions()
                        .position(pos)
                        .title(mItem.title)
                        .snippet(mItem.snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker(mItem.iconColorHue))
                )
                boundsBuilder.include(pos)
                hasPoints = true
            }

            // 5. Draw Travel Route Polyline
            val fullPath = mutableListOf<LatLng>()
            originLocation?.let { fullPath.add(it) } ?: userLocation?.let { fullPath.add(it) }
            fullPath.addAll(routeWaypoints)
            destinationLocation?.let { if (!fullPath.contains(it)) fullPath.add(it) }

            if (fullPath.size >= 2) {
                map.addPolyline(
                    PolylineOptions()
                        .addAll(fullPath)
                        .color(android.graphics.Color.parseColor("#0284C7"))
                        .width(10f)
                        .geodesic(true)
                )
            }

            // 5. Initial Camera Positioning & Marker Click Listener
            map.setOnMarkerClickListener { marker ->
                val clicked = additionalMarkers.firstOrNull {
                    Math.abs(it.latitude - marker.position.latitude) < 0.0005 &&
                    Math.abs(it.longitude - marker.position.longitude) < 0.0005
                }
                if (clicked != null) {
                    onMarkerClick(clicked)
                    true
                } else {
                    false
                }
            }

            if (cameraFocusTarget != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraFocusTarget, 15f))
            } else if (userLocation != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel))
            } else if (hasPoints) {
                try {
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 120))
                } catch (_: Exception) {
                    destinationLocation?.let {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, zoomLevel))
                    }
                }
            }
        } catch (_: Exception) {}
    }

    // Dynamic Camera Movements (Focus Target, Fit All Members, Recenter)
    LaunchedEffect(googleMapInstance, cameraFocusTarget, triggerFitAll, triggerRecenter) {
        val map = googleMapInstance ?: return@LaunchedEffect
        try {
            if (cameraFocusTarget != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraFocusTarget, 15f))
            } else if (triggerFitAll > 0L) {
                val builder = LatLngBounds.Builder()
                var hasAny = false
                originLocation?.let { builder.include(it); hasAny = true }
                userLocation?.let { builder.include(it); hasAny = true }
                destinationLocation?.let { builder.include(it); hasAny = true }
                routeWaypoints.forEach { builder.include(it); hasAny = true }
                additionalMarkers.forEach { builder.include(LatLng(it.latitude, it.longitude)); hasAny = true }
                if (hasAny) {
                    try {
                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 120))
                    } catch (_: Exception) {
                        (originLocation ?: destinationLocation ?: userLocation)?.let {
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, zoomLevel))
                        }
                    }
                }
            } else if (triggerRecenter > 0L && userLocation != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15.5f))
            }
        } catch (_: Exception) {}
    }

    Box(modifier = modifier) {
        if (isMapAvailable) {
            AndroidView(
                factory = {
                    mapView.apply {
                        getMapAsync { map ->
                            googleMapInstance = map
                            try {
                                map.uiSettings.isZoomControlsEnabled = false
                                map.uiSettings.isMyLocationButtonEnabled = false
                                map.uiSettings.isCompassEnabled = true
                            } catch (_: Exception) {}
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Graceful notice when Google Maps services require developer API key setup
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PastelCardBlue)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🗺️", fontSize = 34.sp)
                    Text(
                        text = "Google Maps Live GPS View",
                        color = TextNavyDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = userLocation?.let { "GPS: ${it.latitude.toString().take(7)}, ${it.longitude.toString().take(7)}" }
                            ?: "Waiting for GPS signal...",
                        color = SkyBlueDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "To enable standard satellite tiles, configure Google Maps API key in AndroidManifest.xml.",
                        color = TextMutedSlate,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Overlay Map Controls (Zoom In, Zoom Out, Recenter My Location)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Recenter My Location Button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(SurfaceWhite)
                    .border(1.dp, BorderLight, CircleShape)
                    .clickable {
                        onRecenterClick()
                        userLocation?.let { uLoc ->
                            googleMapInstance?.animateCamera(CameraUpdateFactory.newLatLngZoom(uLoc, 15.5f))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎯", fontSize = 18.sp)
            }

            // Zoom In Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(SurfaceWhite)
                    .border(1.dp, BorderLight, CircleShape)
                    .clickable {
                        googleMapInstance?.animateCamera(CameraUpdateFactory.zoomIn())
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "+", color = TextNavyDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            // Zoom Out Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(SurfaceWhite)
                    .border(1.dp, BorderLight, CircleShape)
                    .clickable {
                        googleMapInstance?.animateCamera(CameraUpdateFactory.zoomOut())
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "−", color = TextNavyDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
