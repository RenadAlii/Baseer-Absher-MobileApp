package com.baseer.baseer.presentation.components.topSnackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopSnackbar(
    snackbarEvent: SnackbarEvent,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shadowElevation = 66.dp,
        contentColor = Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        AnimatedVisibility(
            visible = SnackbarController.hasEvent(),
            enter = fadeIn() + expandVertically(),
            exit = shrinkVertically(
                animationSpec = slowShrinkTween(),
                shrinkTowards = Alignment.Top
            )
        ) {
            val event = remember(this) { snackbarEvent as SnackbarEvent.Show }

            Row(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth()
                    .background(color = CardBackground)
                    .clip(RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {

                VerticalDivider(
                    color = event.backgroundColor(),
                    thickness = 8.dp
                )

                IconContainer(
                    isSuccess = event is SnackbarEvent.Show.Success,
                    iconTint = event.backgroundColor(),
                    iconRes = event.iconId,
                )

                SnackbarTexts(
                    title = stringResource(event.message),
                    description = event.description?.let { stringResource(it) },
                )
            }
        }
    }
}


@Composable
private fun IconContainer(
    isSuccess: Boolean,
    iconTint: Color,
    iconRes: DrawableResource,
) {
    val bgColor =
        if (isSuccess) SuccessBackgroundLight else ErrorBackgroundLight

    Box(
        modifier = Modifier
            .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
            .clip(CircleShape)
            .background(color = bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.padding(8.dp),
            tint = iconTint
        )
    }
}

@Composable
private fun SnackbarTexts(
    title: String,
    description: String?,
) {
    Column(
        modifier = Modifier
            .padding(start = 12.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            style =
                TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TitleTextColor,
                    textAlign = TextAlign.Start
                )
        )
        if (description != null) {
            Text(
                text = description,
                modifier = Modifier.padding(top = 4.dp),
                style =
                    TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = DescriptionTextColor,
                        textAlign = TextAlign.Start
                    )
            )
        }
    }
}

private fun slowShrinkTween(): FiniteAnimationSpec<IntSize> =
    tween(
        durationMillis = 400,
        easing = FastOutSlowInEasing
    )