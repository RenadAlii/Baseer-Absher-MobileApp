package com.baseer.baseer.presentation.components.topSnackbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import baseer.composeapp.generated.resources.Res
import baseer.composeapp.generated.resources.ic_error
import baseer.composeapp.generated.resources.ic_success
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class SnackbarEvent {

    sealed class Show(
        open val message: StringResource,
        open val description: StringResource? = null,
        open val iconId: DrawableResource,
    ) : SnackbarEvent() {

        data class Success(
            override val message: StringResource,
            override val description: StringResource? = null,
            override val iconId: DrawableResource = Res.drawable.ic_success,
        ) : Show(
            message = message,
            description = description,
            iconId = iconId,
        ) {
            @Composable
            override fun backgroundColor(): Color = SuccessColor
        }

        data class Error(
            override val message: StringResource,
            override val description: StringResource? = null,
            override val iconId: DrawableResource = Res.drawable.ic_error,
        ) : Show(
            message = message,
            description = description,
            iconId = iconId,
        ) {
            @Composable
            override fun backgroundColor(): Color = ErrorColor
        }

        @Composable
        abstract fun backgroundColor(): Color
    }

    data object None : SnackbarEvent()
}

// Base colors inspired by your screenshots
val CardBackground = Color(0xFFFFFFFF)
val TitleTextColor = Color(0xFF111827)
val DescriptionTextColor = Color(0xFF6B7280)

val SuccessColor = Color(0xFF22C55E)
val SuccessBackgroundLight = Color(0xFFE5F9ED)

val ErrorColor = Color(0xFFEF4444)
val ErrorBackgroundLight = Color(0xFFFDECEC)