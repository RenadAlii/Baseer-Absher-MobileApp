package com.baseer.baseer.presentation.reportSelect.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import baseer.composeapp.generated.resources.*
import com.baseer.baseer.presentation.components.EditableInfoBox
import com.baseer.baseer.presentation.components.TopMainAppBar
import com.baseer.baseer.presentation.reportSelect.componenets.ReportTypeItem
import com.baseer.baseer.presentation.reportSelect.viewmodel.ReportSelectionViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReportSelectionScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ReportSelectionViewModel = viewModel { ReportSelectionViewModel() },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    ReportSelectionContent(
        state = state,
        modifier = modifier,
        onEvent = viewModel::onEvent,
        navController = navController
    )
}

@Composable
private fun ReportSelectionContent(
    navController: NavController,
    state: ReportSelectionState,
    modifier: Modifier = Modifier,
    onEvent: (ReportSelectionEvent) -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopMainAppBar(
                modifier = modifier,
                title = stringResource(Res.string.report_screen_title)
            ) {
                navController.popBackStack()
            }
        },
        bottomBar = {
            Call911Button(
                onClick = { onEvent(ReportSelectionEvent.OnCall911Click) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Location Box
            item {
                state.location?.let { location ->
                    EditableInfoBox(
                        title = stringResource(Res.string.report_location_label),
                        value = location.address.orEmpty(),
                        buttonText = stringResource(Res.string.report_change_location),
                        icon = painterResource(Res.drawable.ic_pin_location),
                        onEditClick = { onEvent(ReportSelectionEvent.OnLocationEditClick) }
                    )
                }
            }

            // Section Title
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.report_type_header),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Report Types List
            items(
                items = state.reportTypes,
                key = { it.id }
            ) { reportType ->
                ReportTypeItem(
                    title = stringResource(reportType.titleRes),
                    icon = painterResource(reportType.icon),
                    onClick = { onEvent(ReportSelectionEvent.OnReportTypeClick(reportType)) }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun Call911Button(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = stringResource(Res.string.report_call_911),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}