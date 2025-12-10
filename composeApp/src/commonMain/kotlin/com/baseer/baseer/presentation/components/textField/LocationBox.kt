package com.baseer.baseer.presentation.components.textField

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import baseer.composeapp.generated.resources.*
import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.presentation.components.EditableInfoBox
import com.baseer.baseer.presentation.components.model.LocationBoxUiState
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LocationBox(
    uiState: LocationBoxUiState,
    onEditClick: (LocationData) -> Unit,
    onRetryClick: () -> Unit
) {
    when (uiState) {
        LocationBoxUiState.Loading -> {
            EditableInfoBox(
                title = stringResource(Res.string.report_location_label),
                value = stringResource(Res.string.location_loading),
                buttonText = stringResource(Res.string.report_change_location),
                icon = painterResource(Res.drawable.ic_pin_location),
                onEditClick = { }
            )
        }

        is LocationBoxUiState.LocationAvailable -> {
            EditableInfoBox(
                title = stringResource(Res.string.report_location_label),
                value = uiState.locationDisplayText,
                buttonText = stringResource(Res.string.report_change_location),
                icon = painterResource(Res.drawable.ic_pin_location),
                onEditClick = { onEditClick(uiState.location) }
            )
        }

        is LocationBoxUiState.PermissionRequired -> {
            LocationPermissionCard(
                onRetryClick = onRetryClick,
                permissionState = uiState.permissionState
            )
        }
    }
}

@Composable
private fun LocationPermissionCard(
    onRetryClick: () -> Unit,
    permissionState: PermissionState
) {
    val permissionsController = rememberPermissionsControllerFactory().createPermissionsController()

    val action: () -> Unit = when (permissionState) {
        PermissionState.DeniedAlways -> {
            { permissionsController.openAppSettings() }
        }

        else -> {
            onRetryClick
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (permissionState == PermissionState.DeniedAlways) {
                    stringResource(Res.string.permission_denied_always)
                } else {
                    stringResource(Res.string.permission_required)
                },
                fontSize = 14.sp,
                color = Color(0xFFE65100),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = action,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (permissionState == PermissionState.DeniedAlways) {
                        stringResource(Res.string.button_open_settings)
                    } else {
                        stringResource(Res.string.button_locate)
                    },
                )
            }
        }
    }
}