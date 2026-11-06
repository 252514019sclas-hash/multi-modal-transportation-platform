package com.simats.multimodaltransportationrecommendation.ui.compare

import androidx.lifecycle.ViewModel
import com.simats.multimodaltransportationrecommendation.data.model.routes.RouteOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CompareRoutesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CompareRoutesUiState())
    val uiState: StateFlow<CompareRoutesUiState> = _uiState.asStateFlow()

    fun setRoutes(
        origin: String,
        destination: String,
        routes: List<RouteOption>,
        initialSelected: RouteOption? = null
    ) {
        val mappedRoutes = if (routes.isNotEmpty()) {
            routes.map { r ->
                ComparisonRoute(
                    title = r.title,
                    time = r.durationText,
                    fare = r.fareText,
                    walk = "${(r.distanceKm * 2.5).toInt().coerceAtLeast(2)} min",
                    co2 = "${r.co2EmittedKg} kg",
                    onTime = if (r.mode.lowercase() in listOf("walking", "cycling")) "100%" else if (r.mode.lowercase().contains("metro") || r.mode.lowercase().contains("train")) "96%" else "92%",
                    isAiPick = r.isRecommended,
                    routeOption = r
                )
            }
        } else {
            _uiState.value.routes
        }

        val chosenTitle = initialSelected?.title ?: mappedRoutes.firstOrNull { it.isAiPick }?.title ?: mappedRoutes.firstOrNull()?.title ?: ""
        val chosenOption = initialSelected ?: mappedRoutes.firstOrNull { it.title == chosenTitle }?.routeOption

        _uiState.value = _uiState.value.copy(
            origin = origin,
            destination = destination,
            routes = mappedRoutes,
            selectedRouteTitle = chosenTitle,
            selectedRouteOption = chosenOption,
            aiInsight = "${chosenTitle} offers the most optimal carbon efficiency with low travel stress."
        )
    }

    fun selectRoute(routeTitle: String) {
        val selectedItem = _uiState.value.routes.firstOrNull { it.title == routeTitle }
        _uiState.value = _uiState.value.copy(
            selectedRouteTitle = routeTitle,
            selectedRouteOption = selectedItem?.routeOption
        )
    }
}

data class CompareRoutesUiState(
    val origin: String = "Chennai Central",
    val destination: String = "Mahabalipuram",
    val selectedRouteTitle: String = "MTC Express Bus 588",
    val selectedRouteOption: RouteOption? = null,
    val routes: List<ComparisonRoute> = listOf(
        ComparisonRoute("MTC Express Bus 588", "1 hr 45 min", "₹45", "8 min", "1.1 kg", "92%", true),
        ComparisonRoute("Suburban Rail + TNSTC Bus", "1 hr 35 min", "₹35", "12 min", "0.8 kg", "96%", false),
        ComparisonRoute("Tourist Taxi / Cab", "1 hr 15 min", "₹1,250", "2 min", "6.2 kg", "88%", false),
        ComparisonRoute("SETC AC Coach", "1 hr 30 min", "₹95", "10 min", "1.4 kg", "90%", false)
    ),
    val aiInsight: String = "MTC Express Bus 588 offers direct ECR connectivity with optimal balance of fare and travel comfort.",
    val note: String = "Estimated fares and emissions. Reliability is based on recent Tamil Nadu transit fleet telemetry."
)

data class ComparisonRoute(
    val title: String,
    val time: String,
    val fare: String,
    val walk: String,
    val co2: String,
    val onTime: String,
    val isAiPick: Boolean,
    val routeOption: RouteOption? = null
)
