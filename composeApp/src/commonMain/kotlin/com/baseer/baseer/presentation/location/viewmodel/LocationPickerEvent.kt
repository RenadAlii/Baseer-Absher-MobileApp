package com.baseer.baseer.presentation.location.viewmodel

import com.baseer.baseer.domain.model.LocationData

data class LatLng(val latitude: Double, val longitude: Double)

sealed interface LocationPickerEvent {
    data class OnMapClick(val latLng: LatLng) : LocationPickerEvent
    data class OnSearchQueryChange(val query: String) : LocationPickerEvent
    data object OnSearchClick : LocationPickerEvent
    data object OnClearSearch : LocationPickerEvent
    data class OnSearchResultClick(val location: LocationData) : LocationPickerEvent
    data object OnConfirmClick : LocationPickerEvent
    data object OnMyLocationClick : LocationPickerEvent
    data object OnErrorShown : LocationPickerEvent
}