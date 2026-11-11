package com.example.ui.match

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MatchItem
import com.example.data.model.SetScore
import com.example.ui.components.PlayerAvatar
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoseLoss
import com.example.ui.theme.Zinc100
import com.example.ui.theme.Zinc400
import com.example.ui.theme.Zinc700
import com.example.ui.theme.Zinc800
import com.example.ui.theme.Zinc850
import com.example.ui.theme.Zinc900
import com.example.ui.theme.Zinc950

@Composable
fun MatchScoringDialog(
    match: MatchItem,
    maxSets: Int,
    isPersian: Boolean,
    onDismiss: () -> Unit,
    onSave: (List<SetScore>) -> Unit
) {
    // Initial scores list
    val scores1 = remember {
        mutableStateListOf<Int>().apply {
            for (i in 0 until maxSets) {
                val existing = match.sets.getOrNull(i)
                add(existing?.score1 ?: 11)
            }
        }
    }

    val scores2 = remember {
        mutableStateListOf<Int>().apply {
            for (i in 0 until maxSets) {
                val existing = match.sets.getOrNull(i)
                add(existing?.score2 ?: 11)
            }
        }
    }

    val quickWinActive = remember {
        mutableStateListOf<Boolean>().apply {
            for (i in 0 until maxSets) {
                val existing = match.sets.getOrNull(i)
                val isQuickWin = existing != null && (
                    (existing.score1 == 11 && existing.score2 == 9) ||
                    (existing.score1 == 9 && existing.score2 == 11)
                )
                add(isQuickWin)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 680.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Zinc800, RoundedCornerShape(20.dp)),
            color = Zinc900
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isPersian) "ثبت نتیجه مسابقه 🎯" else "Record Match Result 🎯",
                            style = MaterialTheme.typography.titleLarge,
                            color = Zinc100
                        )
                        Text(
                            text = if (isPersian)
                                "دور ${match.round} | بهترین از $maxSets ست"
                            else
                                "Round ${match.round} | Best of $maxSets sets",
                            style = MaterialTheme.typography.bodySmall,
                            color = Zinc400
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Zinc800)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Zinc400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Players matchup header banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Zinc950)
                        .border(1.dp, Zinc800, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PlayerAvatar(name = match.player1)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = match.player1,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = "⚔️",
                        fontSize = 16.sp
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = match.player2,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PlayerAvatar(name = match.player2)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sets List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(maxSets) { setIndex ->
                        SetScoreRow(
                            setNumber = setIndex + 1,
                            player1 = match.player1,
                            player2 = match.player2,
                            score1 = scores1[setIndex],
                            score2 = scores2[setIndex],
                            isQuickWin = quickWinActive[setIndex],
                            isPersian = isPersian,
                            onToggleQuickWin = { active ->
                                quickWinActive[setIndex] = active
                                if (active) {
                                    // Default quick win to player 1
                                    scores1[setIndex] = 11
                                    scores2[setIndex] = 9
                                }
                            },
                            onQuickWinWinnerSelected = { winnerIndex ->
                                if (winnerIndex == 1) {
                                    scores1[setIndex] = 11
                                    scores2[setIndex] = 9
                                } else {
                                    scores1[setIndex] = 9
                                    scores2[setIndex] = 11
                                }
                            },
                            onScore1Changed = { scores1[setIndex] = it },
                            onScore2Changed = { scores2[setIndex] = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (match.isPlayed) {
                        OutlinedButton(
                            onClick = {
                                onSave(emptyList()) // Reset match
                            },
                            modifier = Modifier
                                .weight(0.4f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = RoseLoss
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoseLoss.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = if (isPersian) "پاک کردن" else "Reset",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val sets = (0 until maxSets).map { idx ->
                                SetScore(score1 = scores1[idx], score2 = scores2[idx])
                            }
                            onSave(sets)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("save_match_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PureWhite,
                            contentColor = Zinc950
                        )
                    ) {
                        Text(
                            text = if (isPersian) "ذخیره نتیجه مسابقه 💾" else "Save Result 💾",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SetScoreRow(
    setNumber: Int,
    player1: String,
    player2: String,
    score1: Int,
    score2: Int,
    isQuickWin: Boolean,
    isPersian: Boolean,
    onToggleQuickWin: (Boolean) -> Unit,
    onQuickWinWinnerSelected: (Int) -> Unit,
    onScore1Changed: (Int) -> Unit,
    onScore2Changed: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Zinc950)
            .border(1.dp, Zinc800, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        // Set Header & Quick Win Checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isPersian) "ست $setNumber" else "Set $setNumber",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Zinc100
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleQuickWin(!isQuickWin) }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Checkbox(
                    checked = isQuickWin,
                    onCheckedChange = { onToggleQuickWin(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Emerald500,
                        uncheckedColor = Zinc700,
                        checkmarkColor = PureWhite
                    ),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isPersian) "برد سریع (۱۱ به ۹)" else "Quick Win (11-9)",
                    fontSize = 11.sp,
                    color = Emerald400,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        HorizontalDivider(
            color = Zinc800,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (isQuickWin) {
            // Quick Win Winner Selector Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isP1 = score1 == 11 && score2 == 9
                val isP2 = score2 == 11 && score1 == 9

                QuickWinButton(
                    title = "$player1 (11-9)",
                    isSelected = isP1,
                    modifier = Modifier.weight(1f),
                    onClick = { onQuickWinWinnerSelected(1) }
                )

                QuickWinButton(
                    title = "$player2 (11-9)",
                    isSelected = isP2,
                    modifier = Modifier.weight(1f),
                    onClick = { onQuickWinWinnerSelected(2) }
                )
            }
        } else {
            // Manual Score Steppers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreStepper(
                    playerName = player1,
                    score = score1,
                    isWinner = score1 > score2,
                    onScoreChanged = onScore1Changed,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = ":",
                    fontFamily = JetBrainsMono,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Zinc400,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                ScoreStepper(
                    playerName = player2,
                    score = score2,
                    isWinner = score2 > score1,
                    onScoreChanged = onScore2Changed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickWinButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Emerald500.copy(alpha = 0.2f) else Zinc900)
            .border(
                1.dp,
                if (isSelected) Emerald400 else Zinc800,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Emerald400 else Zinc400,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ScoreStepper(
    playerName: String,
    score: Int,
    isWinner: Boolean,
    onScoreChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = playerName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Zinc400,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Zinc850)
                .border(1.dp, if (isWinner) Emerald400.copy(alpha = 0.5f) else Zinc800, RoundedCornerShape(10.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            IconButton(
                onClick = { if (score > 0) onScoreChanged(score - 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = Zinc400,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = "$score",
                fontFamily = JetBrainsMono,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isWinner) Emerald400 else PureWhite,
                modifier = Modifier
                    .width(36.dp),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = { if (score < 50) onScoreChanged(score + 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = Zinc400,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
