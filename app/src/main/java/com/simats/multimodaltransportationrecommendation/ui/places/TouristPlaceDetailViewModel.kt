package com.simats.multimodaltransportationrecommendation.ui.places

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlaceDetailUiState(
    val place: TouristPlace? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class TouristPlaceDetailViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaceDetailUiState())
    val uiState: StateFlow<PlaceDetailUiState> = _uiState.asStateFlow()

    fun loadPlaceDetails(context: Context, placeId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            when (val result = authRepository.fetchPlaceDetails(placeId, userId)) {
                is NetworkResult.Success -> {
                    val p = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        place = p,
                        isSaved = p.isSaved
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun setPlaceDirect(place: TouristPlace) {
        _uiState.value = _uiState.value.copy(
            place = place,
            isSaved = place.isSaved,
            isLoading = false
        )
    }

    fun toggleSavePlace(context: Context) {
        val currentPlace = _uiState.value.place ?: return
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        val newAction = if (_uiState.value.isSaved) "remove" else "save"

        _uiState.value = _uiState.value.copy(isSaving = true)

        viewModelScope.launch {
            when (val result = authRepository.savePlace(userId, currentPlace.placeId, newAction)) {
                is NetworkResult.Success -> {
                    val savedNow = result.data
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        isSaved = savedNow,
                        successMessage = if (savedNow) "Saved to your places!" else "Removed from saved places"
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}

