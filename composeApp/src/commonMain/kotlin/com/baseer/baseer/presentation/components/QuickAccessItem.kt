package com.baseer.baseer.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import baseer.composeapp.generated.resources.Res
import baseer.composeapp.generated.resources.ic_warning
import org.jetbrains.compose.resources.painterResource

@Composable
fun QuickAccessItem(
    title: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    hasWarningIcon: Boolean = false,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Warning icon if needed
            if (hasWarningIcon) {
                Icon(
                    painter = painterResource(Res.drawable.ic_warning),
                    contentDescription = null,
                    tint = Color(0xFFFFA000),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Image(
                painter = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.Gray,
                    )
                }
            }

        }
    }
}