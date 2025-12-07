package com.baseer.baseer

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.baseer.baseer.presentation.navigation.AppNavigationGraph
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        AppNavigationGraph()
    }
}