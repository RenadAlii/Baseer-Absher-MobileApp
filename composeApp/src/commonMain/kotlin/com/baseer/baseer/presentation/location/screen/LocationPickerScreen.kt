package com.baseer.baseer.presentation.location.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import baseer.composeapp.generated.resources.*
import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.presentation.components.PrimaryButton
import com.baseer.baseer.presentation.components.TopMainAppBar
import com.baseer.baseer.presentation.location.viewmodel.LatLng
import com.baseer.baseer.presentation.location.viewmodel.LocationPickerEvent
import com.baseer.baseer.presentation.location.viewmodel.LocationPickerState
import com.baseer.baseer.presentation.location.viewmodel.LocationPickerViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LocationPickerScreen(
    initialLatitude: Double,
    initialLongitude: Double,
    initialAddress: String?,
    currentLocatingLatitude: Double?,
    currentLocatingLongitude: Double?,
    currentLocatingAddress: String?,
    onLocationConfirmed: (LocationData) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LocationPickerViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initialize(
            latitude = initialLatitude,
            longitude = initialLongitude,
            address = initialAddress,
            currentLat = currentLocatingLatitude,
            currentLng = currentLocatingLongitude,
            currentAddress = currentLocatingAddress
        )
        viewModel.setOnLocationConfirmed { location ->
            onLocationConfirmed(location)
        }
    }

    LocationPickerContent(
        modifier = modifier,
        state = state,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick
    )
}

@Composable
private fun LocationPickerContent(
    state: LocationPickerState,
    onEvent: (LocationPickerEvent) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NativeMapView(
            modifier = Modifier.fillMaxSize(),
            selectedLocation = state.selectedLocation,
            onMapClick = { lat, lng ->
                onEvent(LocationPickerEvent.OnMapClick(LatLng(lat, lng)))
            }
        )

        Column(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            TopMainAppBar(
                withBackText = false,
                backgroundColor = Color.Transparent,
                title = stringResource(Res.string.location_picker_title),
                onBackClick = onBackClick
            )
            SearchBox(
                query = state.searchQuery,
                isSearching = state.isSearching,
                onQueryChange = { onEvent(LocationPickerEvent.OnSearchQueryChange(it)) },
                onSearchClick = {
                    keyboardController?.hide()
                    onEvent(LocationPickerEvent.OnSearchClick)
                },
                onClearClick = { onEvent(LocationPickerEvent.OnClearSearch) }
            )

            if (state.searchResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                SearchResults(
                    results = state.searchResults,
                    onResultClick = { location ->
                        keyboardController?.hide()
                        onEvent(LocationPickerEvent.OnSearchResultClick(location))
                    }
                )
            }
        }

        FloatingMyLocationButton(
            onClick = { onEvent(LocationPickerEvent.OnMyLocationClick) },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 100.dp)
        )

        PrimaryButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            isLoading = state.isLoadingAddress,
            text = stringResource(Res.string.location_picker_confirm),
            onClick = { onEvent(LocationPickerEvent.OnConfirmClick) }
        )
    }
}

@Composable
private fun FloatingMyLocationButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .padding(start = 16.dp, bottom = 50.dp),
        containerColor = Color.White,
        contentColor = Color(0x602E7D32)
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_locating),
            contentDescription = "My Location",
            modifier = Modifier.size(24.dp)
        )
    }
}


@Composable
private fun SearchBox(
    query: String,
    isSearching: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Card(
        modifier = Modifier.padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(Res.string.location_picker_search_hint),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClearClick) {
                        Image(
                            painter = painterResource(Res.drawable.ic_exit),
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            leadingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF2E7D32)
                    )
                } else {
                    IconButton(onClick = onSearchClick) {
                        Image(
                            painter = painterResource(Res.drawable.ic_search),
                            contentDescription = "Search",
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color(0xFF2E7D32),
                focusedContainerColor = Color.White.copy(alpha = 0.85f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.85f),
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchClick() })
        )
    }
}

@Composable
private fun SearchResults(
    results: List<LocationData>,
    onResultClick: (LocationData) -> Unit
) {
    Card(
        modifier = Modifier.padding(horizontal = 24.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            items(
                items = results,
                key = { "${it.latitude}_${it.longitude}" }
            ) { location ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onResultClick(location) }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = location.address ?: "${location.latitude}, ${location.longitude}",
                            color = Color.Black,
                            fontSize = 14.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (results.last() != location) {
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }
                }
            }
        }
    }
}

