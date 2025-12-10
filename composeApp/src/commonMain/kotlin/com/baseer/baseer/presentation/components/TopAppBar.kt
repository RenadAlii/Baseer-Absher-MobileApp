package com.baseer.baseer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import baseer.composeapp.generated.resources.Res
import baseer.composeapp.generated.resources.ic_arrow_right
import baseer.composeapp.generated.resources.report_back
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopMainAppBar(
    title: String,
    modifier: Modifier = Modifier,
    withBackText: Boolean = true,
    backgroundColor: Color = Color(0xFFF5F5F5),
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .then(
                if (backgroundColor == Color.Transparent) {
                    Modifier
                } else {
                    Modifier.background(backgroundColor)
                }
            )
    ) {
        // Back button — aligned start
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            TextButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = Color(0xFF1B8354)
                )
                if (withBackText) {
                    Text(
                        text = stringResource(Res.string.report_back),
                        color = Color(0xFF1B8354),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF161616),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}