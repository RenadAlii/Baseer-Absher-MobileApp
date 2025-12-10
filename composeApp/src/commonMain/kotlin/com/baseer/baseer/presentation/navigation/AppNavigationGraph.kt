package com.baseer.baseer.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.baseer.baseer.domain.model.LocationData
import com.baseer.baseer.presentation.components.topSnackbar.ObserveSnackbarEvent
import com.baseer.baseer.presentation.components.topSnackbar.SnackbarController
import com.baseer.baseer.presentation.components.topSnackbar.TopSnackbar
import com.baseer.baseer.presentation.details.screen.ReportDetailsScreen
import com.baseer.baseer.presentation.home.screen.HomeScreen
import com.baseer.baseer.presentation.location.screen.LocationPickerScreen
import com.baseer.baseer.presentation.reportSelect.screen.ReportSelectionScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationGraph() {

    val navController = rememberNavController()
    val scrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        contentColor = Color.Transparent,
    ) { padding ->
        Box {
            NavHost(
                modifier = Modifier.align(Alignment.TopCenter)
                    .padding(
                        bottom = padding.calculateBottomPadding(),
                    ),
                navController = navController,
                startDestination = AppScreens.Home,
                // Explicitly specifying transitions turns off default animations
                // in favor of the selected ones
                enterTransition = { fadeIn() + slideInHorizontally() },
                exitTransition = { fadeOut() }
            ) {

                composable<AppScreens.Home> {
                    HomeScreen(
                        modifier = Modifier.padding(top = padding.calculateTopPadding()),
                        onNavigateToReportSelection = {
                            navController.navigate(AppScreens.ReportSelection)
                        }
                    )
                }

                composable<AppScreens.ReportSelection> {
                    ReportSelectionScreen(
                        modifier = Modifier.padding(top = padding.calculateTopPadding()),
                        navController = navController
                    )
                }

                composable<AppScreens.LocationPicker> { backStackEntry ->
                    val args = backStackEntry.toRoute<AppScreens.LocationPicker>()

                    LocationPickerScreen(
                        modifier = Modifier.padding(top = padding.calculateTopPadding()),
                        initialLatitude = args.selectedLatitude,
                        initialLongitude = args.selectedLongitude,
                        initialAddress = args.selectedAddress,
                        currentLocatingLatitude = args.currentLocatingLatitude,
                        currentLocatingLongitude = args.currentLocatingLongitude,
                        currentLocatingAddress = args.currentLocatingAddress,
                        onLocationConfirmed = { location ->
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("selected_location", location)
                            navController.popBackStack()
                        },
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<AppScreens.ReportDetails> { backStackEntry ->
                    val args = backStackEntry.toRoute<AppScreens.ReportDetails>()

                    val initialLocation = LocationData(
                        latitude = args.initialLatitude,
                        longitude = args.initialLongitude,
                        address = args.initialAddress
                    )

                    ReportDetailsScreen(
                        navController = navController,
                        reportTypeId = args.reportTypeId,
                        initialLocation = initialLocation,
                        modifier = Modifier.padding(top = padding.calculateTopPadding()),
                    )
                }

            }
            ObserveSnackbarEvent(
                state = SnackbarController.event,
                content = {
                    TopSnackbar(
                        it,
                        modifier = Modifier.padding(8.dp).padding(top = padding.calculateTopPadding())
                    )
                }
            )
        }
    }
}