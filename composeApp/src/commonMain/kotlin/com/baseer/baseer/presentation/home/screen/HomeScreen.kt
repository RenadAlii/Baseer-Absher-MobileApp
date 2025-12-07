package com.baseer.baseer.presentation.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import baseer.composeapp.generated.resources.*
import com.baseer.baseer.presentation.components.QuickAccessCard
import com.baseer.baseer.presentation.components.QuickAccessItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToReportSelection: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = stringResource(Res.string.home_quick_access),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
            )

            // Quick Access Items
            QuickAccessSection(onNavigateToReportSelection)
        }
    }
}

@Composable
private fun QuickAccessSection(
    onNavigateToReportSelection: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickAccessCard(
            title = stringResource(Res.string.home_submit_report),
            subtitle = stringResource(Res.string.home_submit_report_subtitle),
            icon = painterResource(Res.drawable.ic_report),
            onClick = onNavigateToReportSelection
        )

        QuickAccessCard(
            title = stringResource(Res.string.home_my_vehicles),
            subtitle = stringResource(Res.string.home_my_vehicles_subtitle),
            icon = painterResource(Res.drawable.ic_my_car),
            onClick = {

            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.home_authentication),
                icon = painterResource(Res.drawable.ic_authentication),
                onClick = {

                }
            )
            QuickAccessItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.home_absher_travel),
                icon = painterResource(Res.drawable.ic_absher_map),
                onClick = {

                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.home_traffic_violations),
                icon = painterResource(Res.drawable.ic_accident),
                onClick = {

                }
            )
            QuickAccessItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.home_simple_accident),
                icon = painterResource(Res.drawable.ic_accident),
                onClick = {

                }
            )
        }

        QuickAccessCard(
            title = stringResource(Res.string.home_my_weapons),
            subtitle = stringResource(Res.string.home_my_weapons_subtitle),
            icon = painterResource(Res.drawable.ic_weapons),
            onClick = {

            }
        )
    }
}