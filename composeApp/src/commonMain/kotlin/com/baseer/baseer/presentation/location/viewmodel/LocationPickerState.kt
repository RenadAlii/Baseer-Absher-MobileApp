package com.baseer.baseer.presentation.location.viewmodel

import com.baseer.baseer.domain.model.LocationData

data class LocationPickerState(
    val selectedLocation: LocationData? = null,
    val searchQuery: String = "",
    val searchResults: List<LocationData> = emptyList(),
    val isSearching: Boolean = false,
    val isLoadingAddress: Boolean = false,
    val isLoadingCurrentLocation: Boolean = false,
    val error: String? = null
)