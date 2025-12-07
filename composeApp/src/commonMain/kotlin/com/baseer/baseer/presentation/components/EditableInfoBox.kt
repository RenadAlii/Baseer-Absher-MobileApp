package com.baseer.baseer.presentation.components

import androidx.compose.foundation.BorderStroke
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
import baseer.composeapp.generated.resources.ic_pin_location
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditableInfoBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    buttonText: String = "تعديل",
    icon: Painter,
    iconTint: Color = Color(0xFF2E7D32),
    buttonColor: Color = Color(0xFF2E7D32),
    onEditClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEditClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Info Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {


                Icon(
                    painter = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = title,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = value,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

            }

            Text(
                text = buttonText,
                color = buttonColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview
@Composable
private fun EditableInfoBoxPreview() {

    EditableInfoBox(
        title = "التاريخ",
        value = "١٥ ديسمبر ٢٠٢٤",
        buttonText = "تغيير",
        icon = painterResource(Res.drawable.ic_pin_location),
        onEditClick = { /* افتح منتقي التاريخ */ }
    )
}
