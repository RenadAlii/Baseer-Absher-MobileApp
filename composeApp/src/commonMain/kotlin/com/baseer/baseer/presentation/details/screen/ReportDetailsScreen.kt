package com.baseer.baseer.presentation.details.screen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import baseer.composeapp.generated.resources.*
import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.presentation.components.AlertCard
import com.baseer.baseer.presentation.components.AlertType
import com.baseer.baseer.presentation.components.PrimaryButton
import com.baseer.baseer.presentation.components.TopMainAppBar
import com.baseer.baseer.presentation.components.inlineHelper.HelperMessageType
import com.baseer.baseer.presentation.components.keybord.DismissKeyboardOnClick
import com.baseer.baseer.presentation.components.model.ReportType
import com.baseer.baseer.presentation.components.textField.LabeledOutlinedTextFieldInput
import com.baseer.baseer.presentation.components.textField.LocationBox
import com.baseer.baseer.presentation.details.viewmodel.ReportDetailsEvent
import com.baseer.baseer.presentation.details.viewmodel.ReportDetailsState
import com.baseer.baseer.presentation.details.viewmodel.ReportDetailsViewModel
import com.baseer.baseer.presentation.navigation.AppScreens
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReportDetailsScreen(
    navController: NavController,
    reportTypeId: String,
    initialLocation: LocationData,
    modifier: Modifier = Modifier,
    viewModel: ReportDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val reportType = state.reportType

    LaunchedEffect(reportTypeId) {
        viewModel.processEvent(ReportDetailsEvent.LoadInitialData(reportTypeId, initialLocation))
    }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow<LocationData?>("selected_location", null)
            ?.collect { location ->
                location?.let {
                    viewModel.processEvent(ReportDetailsEvent.LocationUpdated(it))
                    savedStateHandle.remove<LocationData>("selected_location")
                }
            }
    }

    if (reportType != null) {
        ReportDetailsContent(
            state = state,
            reportType = reportType,
            modifier = modifier,
            onEvent = viewModel::processEvent,
            onBackClick = { navController.popBackStack() },
            onEditLocationClick = { location ->
                navController.navigate(
                    AppScreens.LocationPicker(
                        selectedLatitude = location.latitude,
                        selectedLongitude = location.longitude,
                        selectedAddress = location.address,
                        currentLocatingLatitude = location.latitude,
                        currentLocatingLongitude = location.longitude,
                        currentLocatingAddress = location.address
                    )
                )
            }
        )
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun ReportDetailsContent(
    state: ReportDetailsState,
    reportType: ReportType,
    modifier: Modifier = Modifier,
    onEvent: (ReportDetailsEvent) -> Unit,
    onBackClick: () -> Unit,
    onEditLocationClick: (LocationData) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopMainAppBar(
                modifier = modifier,
                title = stringResource(reportType.titleRes),
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = stringResource(Res.string.button_confirm_send),
                enabled = state.isSendEnabled,
            ) {
                onEvent(ReportDetailsEvent.SendReportClick)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                DismissKeyboardOnClick(
                    interactionSource = interactionSource,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        AlertCard(
                            title = stringResource(Res.string.report_alert_title),
                            description = stringResource(Res.string.report_alert_description),
                            type = AlertType.DANGER,
                            showClose = false,
                            icon = Res.drawable.ic_info
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        LocationBox(
                            uiState = state.locationBoxUiState,
                            onEditClick = { location -> onEditLocationClick(location) },
                            onRetryClick = { onEditLocationClick(state.location) }
                        )


                        Spacer(modifier = Modifier.height(24.dp))

                        if (state.showPlateNumberField) {
                            LabeledOutlinedTextFieldInput(
                                title = stringResource(Res.string.plate_number_title),
                                value = state.plateNumber,
                                onValueChange = { onEvent(ReportDetailsEvent.PlateNumberChange(it)) },
                                placeholder = stringResource(Res.string.plate_number_placeholder),
                                required = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                autoAlignByLanguage = true,
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }


                        LabeledOutlinedTextFieldInput(
                            title = stringResource(if (state.isDescriptionRequired) Res.string.description_required_title else Res.string.description_optional_title),
                            value = state.description,
                            onValueChange = { onEvent(ReportDetailsEvent.DescriptionChange(it)) },
                            placeholder = stringResource(Res.string.description_placeholder),
                            required = state.isDescriptionRequired,
                            singleLine = false,
                            minLines = 3,
                            autoAlignByLanguage = true,
                            modifier = Modifier.heightIn(min = 100.dp),
                            helperMessage = stringResource(Res.string.description_helper_message),
                            helperType = HelperMessageType.INFO
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // todo add upload files secteing here
                    }
                }
            }
        }
    }
}