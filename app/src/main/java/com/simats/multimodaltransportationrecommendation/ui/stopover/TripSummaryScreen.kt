package com.simats.multimodaltransportationrecommendation.ui.stopover

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.simats.multimodaltransportationrecommendation.data.model.stopover.AiStopoverEngine
import com.simats.multimodaltransportationrecommendation.ui.theme.*

/**
 * TripSummaryScreen: Journey Completed screen displayed after the AI Smart Stopover screen.
 * Perfectly styled with the app's default signature Sky Blue & Teal theme.
 *
 * Requirements fulfilled:
 * 1. Matches reference layout from user screenshot (media_1795064689136_daf332ae.jpg)
 * 2. Styled in app's default theme (Sky Blue, Teal, Navy Dark, Soft Cream), NOT purple.
 * 3. EXPLICITLY REMOVED: "Back to Home" and "Share Your Trip" buttons per user instructions.
 * 4. Shows Hero celebration badge, destination photo card with location badge,
 *    Trip Overview (Distance, Travel Time, Rating), Your Journey Highlights checklist
 *    with Polaroid snapshot & camera illustration, airplane divider, and Tamil Nadu
 *    heritage skyline silhouette.
 */
@Composable
fun TripSummaryScreen(
    origin: String = "Chennai",
    destination: String = "Salem",
    onBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    // Generate accurate route metrics and spots for the chosen Tamil Nadu origin-destination pair
    val plan = remember(origin, destination) {
        AiStopoverEngine.generateStopoverPlan(origin, destination)
    }

    val (destPhotoUrl, destCityName) = remember(destination, plan) {
        resolveDestinationPhotoAndCity(destination, plan.stops)
    }

    val spotCount = if (plan.stops.isNotEmpty()) plan.stops.size else 4
    val distanceDisplay = "${plan.totalDistanceKm.toInt()} km"
    val durationDisplay = plan.totalTravelTimeText.replace("~", "").trim()

    // App signature colors
    val appPrimaryBlue = SkyBluePrimary // 0xFF0284C7
    val appTealSecondary = TealSecondary // 0xFF0D9488
    val appLightTint = SkyBlueLight // 0xFFE0F2FE
    val appNavyDark = TextNavyDark // 0xFF0F172A
    val appSlateMuted = TextMutedSlate // 0xFF64748B
    val appCardBorder = BorderLight // 0xFFE2E8F0
    val appStarGold = SunshineGold // 0xFFEAB308

    val pinGradient = Brush.linearGradient(
        listOf(appPrimaryBlue, appTealSecondary)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoftCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ==========================================
            // 1. TOP APP BAR (Back Button + Centered Title)
            // Clean title with NO category overline
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                // Back Button
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(42.dp)
                        .shadow(2.dp, CircleShape)
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .border(1.dp, appCardBorder, CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "‹",
                        color = appNavyDark,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Title: Trip Summary
                Text(
                    text = "Trip Summary",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = appNavyDark,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ==========================================
            // 2. SCROLLABLE CONTENT BODY
            // ==========================================
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 18.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ------------------------------------------
                // A. HERO CELEBRATION BADGE & TITLE
                // ------------------------------------------
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                ) {
                    // Location Pin with Checkmark Badge + Floating Sparkles
                    Box(
                        modifier = Modifier.size(76.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background soft glow
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(appLightTint.copy(alpha = 0.7f))
                        )

                        // Main Drop-Pin Gradient Badge
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(pinGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Sparkles around badge
                        Text(
                            text = "✨",
                            fontSize = 15.sp,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = 2.dp, top = 2.dp)
                        )
                        Text(
                            text = "🎉",
                            fontSize = 13.sp,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(end = 4.dp, top = 6.dp)
                        )
                        Text(
                            text = "✦",
                            color = appTealSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 6.dp, bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Journey Completed!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = appNavyDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hope you had a wonderful trip",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = appSlateMuted
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "✨", fontSize = 13.sp)
                    }
                }

                // ------------------------------------------
                // B. FEATURED DESTINATION PHOTO CARD
                // ------------------------------------------
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(195.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // High-res scenic photo of the destination
                        AsyncImage(
                            model = destPhotoUrl,
                            contentDescription = destination,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp)),
                            contentScale = ContentScale.Crop
                        )

                        // Subtle dark gradient vignette for readable text overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.65f)
                                        )
                                    )
                                )
                        )

                        // Bottom-Left Destination Overlay Pill
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.45f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "📍", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = destination,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = destCityName,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // ------------------------------------------
                // C. TRIP OVERVIEW METRICS CARD
                // ------------------------------------------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, appCardBorder, RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        // Card Header with Trophy
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Text(text = "🏆", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Trip Overview",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = appNavyDark
                            )
                        }

                        // 3 Metrics Columns (Distance, Travel Time, Rating)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Total Distance
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "📍", fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = distanceDisplay,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = appNavyDark
                                )
                                Text(
                                    text = "Total Distance",
                                    fontSize = 11.sp,
                                    color = appSlateMuted
                                )
                            }

                            // Divider
                            VerticalDivider(
                                modifier = Modifier.height(36.dp),
                                color = appCardBorder
                            )

                            // 2. Travel Time
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🕒", fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = durationDisplay,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = appNavyDark
                                )
                                Text(
                                    text = "Travel Time",
                                    fontSize = 11.sp,
                                    color = appSlateMuted
                                )
                            }

                            // Divider
                            VerticalDivider(
                                modifier = Modifier.height(36.dp),
                                color = appCardBorder
                            )

                            // 3. Overall Rating
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "⭐", fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "4.8 / 5",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = appNavyDark
                                )
                                Text(
                                    text = "Overall Rating",
                                    fontSize = 11.sp,
                                    color = appSlateMuted
                                )
                            }
                        }
                    }
                }

                // ------------------------------------------
                // D. YOUR JOURNEY HIGHLIGHTS CARD
                // ------------------------------------------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, appCardBorder, RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        // Card Header with Gallery Icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Text(text = "🖼️", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Your Journey Highlights",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = appNavyDark
                            )
                        }

                        // Checklist + Polaroid & Camera snapshot
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Column: Checklist of achievements
                            Column(
                                modifier = Modifier.weight(1.3f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                JourneyCheckItem(
                                    text = "Explored $spotCount Amazing Tourist Spots",
                                    appPrimaryBlue = appPrimaryBlue,
                                    appNavyDark = appNavyDark
                                )
                                JourneyCheckItem(
                                    text = "Visited $destination",
                                    appPrimaryBlue = appPrimaryBlue,
                                    appNavyDark = appNavyDark
                                )
                                JourneyCheckItem(
                                    text = "Enjoyed the Heritage of $destCityName",
                                    appPrimaryBlue = appPrimaryBlue,
                                    appNavyDark = appNavyDark
                                )
                                JourneyCheckItem(
                                    text = "Created Beautiful Memories!",
                                    appPrimaryBlue = appPrimaryBlue,
                                    appNavyDark = appNavyDark
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Right Column: Polaroid snapshot & Camera illustration
                            Box(
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(130.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Floating sparkle
                                Text(
                                    text = "✨",
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(end = 4.dp)
                                )

                                // Polaroid Frame
                                Box(
                                    modifier = Modifier
                                        .rotate(-4f)
                                        .width(96.dp)
                                        .shadow(4.dp, RoundedCornerShape(8.dp))
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SurfaceWhite)
                                        .border(1.dp, appCardBorder, RoundedCornerShape(8.dp))
                                        .padding(6.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        AsyncImage(
                                            model = destPhotoUrl,
                                            contentDescription = "Memory Snapshot",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(72.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }

                                // 3D Camera badge at bottom-left corner of polaroid
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(start = 2.dp, bottom = 4.dp)
                                        .size(38.dp)
                                        .shadow(3.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(SurfaceWhite)
                                        .border(1.5.dp, appTealSecondary.copy(alpha = 0.35f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "📷", fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // EXPLICIT REQUIREMENT FROM USER:
                // "remove the back to home and share your trip option in my app"
                // The buttons are omitted completely here.
                // ==========================================

                // ------------------------------------------
                // E. FOOTER DIVIDER & DECORATION
                // ------------------------------------------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Airplane Divider: ———— ✈️ ————
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = appCardBorder
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✈️",
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = appCardBorder
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Italic Sign-off Motto
                    Text(
                        text = "Keep Exploring. Keep Travelling! ♡",
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Medium,
                        color = appSlateMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tamil Nadu Heritage Temple & Palm Silhouette Skyline Illustration
                    TamilNaduSkylineCanvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        appPrimaryBlue = appPrimaryBlue,
                        appTealSecondary = appTealSecondary
                    )
                }
            }
        }
    }
}

// ==========================================
// SUB-COMPONENTS
// ==========================================

@Composable
private fun JourneyCheckItem(
    text: String,
    appPrimaryBlue: Color,
    appNavyDark: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(appPrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✔",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = appNavyDark,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Custom Canvas drawing Tamil Nadu temple gopurams and coastal palm tree silhouettes
 * matching the artistic bottom border from the user screenshot in the app's default palette.
 */
@Composable
private fun TamilNaduSkylineCanvas(
    modifier: Modifier = Modifier,
    appPrimaryBlue: Color,
    appTealSecondary: Color
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Soft rolling hill background
        val hillPath = Path().apply {
            moveTo(0f, h * 0.7f)
            cubicTo(
                w * 0.25f, h * 0.5f,
                w * 0.45f, h * 0.85f,
                w * 0.7f, h * 0.65f
            )
            cubicTo(
                w * 0.85f, h * 0.5f,
                w * 0.95f, h * 0.6f,
                w, h * 0.55f
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(hillPath, color = appPrimaryBlue.copy(alpha = 0.08f))

        // 2. Temple Gopuram silhouette on the right side
        val gopuramColor = appPrimaryBlue.copy(alpha = 0.18f)
        val gX = w * 0.82f
        val gBottom = h
        val gWidth = w * 0.14f
        val gHeight = h * 0.68f

        // Draw 5 stepped tiers for the Dravidian Gopuram
        val tiers = 5
        for (i in 0 until tiers) {
            val tierH = gHeight / tiers
            val tierW = gWidth * (1f - (i * 0.12f))
            val left = gX + (gWidth - tierW) / 2f
            val top = gBottom - (i + 1) * tierH
            drawRoundRect(
                color = gopuramColor,
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(tierW, tierH * 1.05f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
            )
        }
        // Kalasam finial at apex
        drawCircle(
            color = gopuramColor,
            radius = 3.5f,
            center = Offset(gX + gWidth / 2f, gBottom - gHeight - 3f)
        )

        // 3. Palm tree silhouette on the left side
        val palmColor = appTealSecondary.copy(alpha = 0.20f)
        val pX = w * 0.08f
        val pY = h * 0.85f

        // Curved trunk
        val trunkPath = Path().apply {
            moveTo(pX, h)
            quadraticTo(pX + 4f, pY + 12f, pX + 8f, pY)
            lineTo(pX + 11f, pY)
            quadraticTo(pX + 7f, pY + 12f, pX + 3f, h)
            close()
        }
        drawPath(trunkPath, color = palmColor)

        // Palm fronds
        val headX = pX + 9.5f
        val headY = pY
        val frondAngles = listOf(-60f, -30f, 0f, 30f, 60f, 90f)
        for (angle in frondAngles) {
            val rad = Math.toRadians(angle.toDouble())
            val fx = headX + (Math.cos(rad) * 16f).toFloat()
            val fy = headY + (Math.sin(rad) * 10f).toFloat()
            drawLine(
                color = palmColor,
                start = Offset(headX, headY),
                end = Offset(fx, fy),
                strokeWidth = 2.2f
            )
        }
    }
}

/**
 * Helper to resolve high-res scenic photos and canonical district/city names
 * across all major Tamil Nadu tourist destinations.
 */
private fun resolveDestinationPhotoAndCity(
    destination: String,
    stops: List<com.simats.multimodaltransportationrecommendation.data.model.stopover.AiStopoverItem>
): Pair<String, String> {
    val clean = destination.trim().lowercase()

    // First check if destination has an en-route stop photo
    val spotMatch = stops.find { it.name.contains(destination, ignoreCase = true) || destination.contains(it.name, ignoreCase = true) }
    if (spotMatch != null && spotMatch.imageUrl.isNotBlank()) {
        return Pair(spotMatch.imageUrl, spotMatch.city)
    }

    return when {
        clean.contains("marina") || clean.contains("chennai") ->
            Pair("https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=800&q=80", "Chennai")

        clean.contains("salem") || clean.contains("yercaud") ->
            Pair("https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&q=80", "Salem")

        clean.contains("coimbatore") || clean.contains("kovai") ->
            Pair("https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?w=800&q=80", "Coimbatore")

        clean.contains("madurai") ->
            Pair("https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=800&q=80", "Madurai")

        clean.contains("thanjavur") || clean.contains("tanjore") ->
            Pair("https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?w=800&q=80", "Thanjavur")

        clean.contains("trichy") || clean.contains("tiruchirappalli") ->
            Pair("https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=800&q=80", "Tiruchirappalli")

        clean.contains("ooty") || clean.contains("nilgiri") ->
            Pair("https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?w=800&q=80", "The Nilgiris")

        clean.contains("kodaikanal") ->
            Pair("https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&q=80", "Dindigul")

        clean.contains("kanyakumari") ->
            Pair("https://images.unsplash.com/photo-1518457607834-6e8d80c183c5?w=800&q=80", "Kanyakumari")

        clean.contains("rameswaram") ->
            Pair("https://images.unsplash.com/photo-1596401057633-54a8fe8ef647?w=800&q=80", "Ramanathapuram")

        clean.contains("kanchipuram") ->
            Pair("https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=800&q=80", "Kanchipuram")

        clean.contains("mahabalipuram") ->
            Pair("https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=800&q=80", "Chengalpattu")

        clean.contains("vellore") ->
            Pair("https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?w=800&q=80", "Vellore")

        clean.contains("tirunelveli") ->
            Pair("https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=800&q=80", "Tirunelveli")

        else -> {
            val catalogMatch = AiStopoverEngine.allSpots.find {
                it.city.contains(destination, ignoreCase = true) ||
                it.name.contains(destination, ignoreCase = true) ||
                it.district.contains(destination, ignoreCase = true)
            }
            if (catalogMatch != null) {
                Pair(catalogMatch.imageUrl, catalogMatch.city)
            } else {
                Pair("https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=800&q=80", "Tamil Nadu")
            }
        }
    }
}
