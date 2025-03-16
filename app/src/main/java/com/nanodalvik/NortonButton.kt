package com.nanodalvik

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun NortonButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    Surface(
            modifier = modifier
                .padding(4.dp)
                .clickable(onClick = onClick),
            shape = RectangleShape,
            color = AppColors.buttonBg,
            elevation = 4.dp
    ) {
        Box(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
        ) {
            Text(
                    text = text,
                    color = AppColors.buttonText,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold
            )
        }
    }
}