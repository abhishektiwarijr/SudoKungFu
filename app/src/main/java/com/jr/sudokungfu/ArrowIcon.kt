package com.jr.sudokungfu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ArrowIcon(
    iconType: ArrowIcon,
    onClick: () -> Unit
) {
    val icon = remember {
        if (iconType == ArrowIcon.LEFT) Icons.Default.KeyboardArrowLeft
        else Icons.Default.KeyboardArrowRight
    }
    val iconAltText = remember {
        if (iconType == ArrowIcon.LEFT) "Go back"
        else "Go forward"
    }
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(CircleShape)
            .size(40.dp)
            .background(Color.DarkGray.copy(alpha = 0.5f), CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconAltText
        )
    }
}

enum class ArrowIcon {
    LEFT, RIGHT
}