package com.baseer.baseer.presentation.location.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.domain.service.GeocoderService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocationPickerViewModel(
    private val geocoderService: GeocoderService
) : ViewModel() {

    private val _state = MutableStateFlow(LocationPickerState())
    val state: StateFlow<LocationPickerState> = _state.asStateFlow()

    private var onLocationConfirmed: ((LocationData) -> Unit)? = null
    private var initialCurrentLocation: LocationData? = null

    fun initialize(
        latitude: Double,
        longitude: Double,
        address: String?,
        currentLat: Double?,
        currentLng: Double?,
        currentAddress: String?
    ) {
        val initialLocation = LocationData(
            latitude = latitude,
            longitude = longitude,
            address = address
        )
        if (currentLat != null && currentLng != null) {
            initialCurrentLocation = LocationData(
                latitude = currentLat,
                longitude = currentLng,
                address = currentAddress
            )
        }
        _state.update {
            it.copy(
                selectedLocation = initialLocation,
                searchQuery = address ?: ""
            )
        }
    }


    fun setOnLocationConfirmed(callback: (LocationData) -> Unit) {
        onLocationConfirmed = callback
    }

    fun onEvent(event: LocationPickerEvent) {
        when (event) {
            is LocationPickerEvent.OnMapClick -> onMapClick(event.latLng)
            is LocationPickerEvent.OnSearchQueryChange -> onSearchQueryChange(event.query)
            LocationPickerEvent.OnSearchClick -> onSearchClick()
            LocationPickerEvent.OnClearSearch -> onClearSearch()
            is LocationPickerEvent.OnSearchResultClick -> onSearchResultClick(event.location)
            LocationPickerEvent.OnConfirmClick -> onConfirmClick()
            LocationPickerEvent.OnMyLocationClick -> onMyLocationClick()
            LocationPickerEvent.OnErrorShown -> clearError()
        }
    }

    private fun onMapClick(latLng: LatLng) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedLocation = LocationData(
                        latitude = latLng.latitude,
                        longitude = latLng.longitude,
                        address = it.selectedLocation?.address
                    ),
                    isLoadingAddress = true,
                    searchResults = emptyList()
                )
            }

            val address = geocoderService.getAddressFromCoordinates(
                latitude = latLng.latitude,
                longitude = latLng.longitude
            )

            val newLocation = LocationData(
                latitude = latLng.latitude,
                longitude = latLng.longitude,
                address = address
            )

            _state.update {
                it.copy(
                    selectedLocation = newLocation,
                    searchQuery = address ?: "",
                    isLoadingAddress = false
                )
            }
        }
    }

    private fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    private fun onSearchClick() {
        val query = _state.value.searchQuery.trim()
        if (query.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isSearching = true, searchResults = emptyList()) }

            try {
                val results = geocoderService.searchLocations(query)
                _state.update {
                    it.copy(
                        searchResults = results,
                        isSearching = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSearching = false,
                        error = "فشل في البحث"
                    )
                }
            }
        }
    }

    private fun onClearSearch() {
        _state.update {
            it.copy(
                searchQuery = "",
                searchResults = emptyList()
            )
        }
    }

    private fun onSearchResultClick(location: LocationData) {
        _state.update {
            it.copy(
                selectedLocation = location,
                searchQuery = location.address ?: "",
                searchResults = emptyList()
            )
        }
    }

    private fun onMyLocationClick() {
        try {
            _state.update {
                it.copy(
                    selectedLocation = initialCurrentLocation,
                    searchQuery = initialCurrentLocation?.address.orEmpty(),
                )
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoadingCurrentLocation = false,
                    error = "فشل في تحديد الموقع"
                )
            }
        }
    }

    private fun onConfirmClick() {
        _state.value.selectedLocation?.let { location ->
            onLocationConfirmed?.invoke(location)
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}