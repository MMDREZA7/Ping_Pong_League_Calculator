package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald950
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.Zinc400
import com.example.ui.theme.Zinc800

@Composable
fun StatusPill(
    isPlayed: Boolean,
    isPersian: Boolean,
    modifier: Modifier = Modifier
) {
    val text = if (isPlayed) {
        if (isPersian) "پایان یافته" else "Finished"
    } else {
        if (isPersian) "در جریان" else "Pending"
    }

    val bgColor = if (isPlayed) Zinc800 else Emerald950
    val textColor = if (isPlayed) Zinc400 else Emerald400
    val borderColor = if (isPlayed) Zinc800 else Emerald400.copy(alpha = 0.3f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PlayerAvatar(
    name: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val initial = name.trim().take(1).uppercase()
    Box(
        modifier = modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ScoreChip(
    score1: Int,
    score2: Int,
    modifier: Modifier = Modifier
) {
    val isP1Winner = score1 > score2
    val isP2Winner = score2 > score1

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Zinc800)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$score1",
            fontFamily = JetBrainsMono,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isP1Winner) Emerald400 else Zinc400
        )
        Text(
            text = "-",
            fontFamily = JetBrainsMono,
            fontSize = 11.sp,
            color = Zinc400,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
        Text(
            text = "$score2",
            fontFamily = JetBrainsMono,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isP2Winner) Emerald400 else Zinc400
        )
    }
}
