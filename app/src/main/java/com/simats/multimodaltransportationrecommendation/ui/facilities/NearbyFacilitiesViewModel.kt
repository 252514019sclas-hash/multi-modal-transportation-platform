package com.simats.multimodaltransportationrecommendation.ui.facilities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.model.facilities.FacilityCategorySummary
import com.simats.multimodaltransportationrecommendation.data.model.facilities.TravelFacility
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NearbyFacilitiesUiState(
    val origin: String = "Chennai",
    val destination: String = "Madurai",
    val selectedCity: String = "Madurai",
    val availableCities: List<String> = emptyList(),
    val selectedCategory: String = "all",
    val selectedLandmark: String? = null,
    val availableLandmarks: List<String> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val facilities: List<TravelFacility> = emptyList(),
    val categories: List<FacilityCategorySummary> = emptyList(),
    val searchedLocation: String? = null,
    val targetPlace: String? = null,
    val totalFacilities: Int = 0
)

class NearbyFacilitiesViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NearbyFacilitiesUiState())
    val uiState: StateFlow<NearbyFacilitiesUiState> = _uiState.asStateFlow()

    fun initParams(
        origin: String,
        destination: String,
        initialCity: String? = null,
        initialCategory: String? = null,
        initialLandmark: String? = null
    ) {
        val cleanOrigin = origin.trim().ifEmpty { "Chennai" }
        val cleanDest = destination.trim().ifEmpty { "Madurai" }
        // Always prioritize the searched destination as the default city
        val target = initialCity?.trim()?.ifEmpty { null } ?: cleanDest

        // Build list of quick selectable cities along route, placing destination first
        val cities = mutableListOf<String>()
        cities.add(cleanDest)
        if (!cities.contains(target)) cities.add(target)
        if (!cities.contains(cleanOrigin)) cities.add(cleanOrigin)

        _uiState.update {
            it.copy(
                origin = cleanOrigin,
                destination = cleanDest,
                selectedCity = target,
                availableCities = cities.distinct(),
                selectedCategory = initialCategory ?: "all",
                selectedLandmark = initialLandmark,
                searchQuery = ""
            )
        }

        loadFacilities()
    }

    fun selectCity(city: String) {
        if (_uiState.value.selectedCity == city && _uiState.value.selectedLandmark == null) return
        _uiState.update {
            it.copy(
                selectedCity = city,
                selectedLandmark = null,
                searchQuery = ""
            )
        }
        loadFacilities()
    }

    fun selectLandmark(landmark: String?) {
        val cleanLandmark = if (landmark.isNullOrBlank() || landmark.startsWith("All", ignoreCase = true)) null else landmark
        if (_uiState.value.selectedLandmark == cleanLandmark) return
        _uiState.update { it.copy(selectedLandmark = cleanLandmark) }
        loadFacilities()
    }

    fun selectCategory(categoryId: String) {
        if (_uiState.value.selectedCategory == categoryId) return
        _uiState.update { it.copy(selectedCategory = categoryId) }
        loadFacilities()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadFacilities()
    }

    fun loadFacilities() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.fetchNearbyFacilities(
                city = state.selectedCity,
                origin = state.origin,
                destination = state.destination,
                landmark = state.selectedLandmark,
                category = if (state.selectedCategory == "all") null else state.selectedCategory,
                search = state.searchQuery.ifBlank { null }
            )

            when (result) {
                is NetworkResult.Success -> {
                    val data = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            facilities = data.facilities,
                            categories = data.categories,
                            availableLandmarks = data.availableLandmarks,
                            searchedLocation = data.searchedLocation ?: state.selectedCity,
                            targetPlace = data.targetPlace,
                            totalFacilities = data.totalFacilities
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
}
