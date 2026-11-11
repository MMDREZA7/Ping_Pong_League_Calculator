package com.example.ui.league

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.MatchItem
import com.example.data.model.PlayerStanding
import com.example.data.model.Tournament
import com.example.ui.MatchFilter
import com.example.ui.components.PlayerAvatar
import com.example.ui.components.ScoreChip
import com.example.ui.components.StatusPill
import com.example.ui.match.MatchScoringDialog
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BronzeChampionship
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoseLoss
import com.example.ui.theme.SilverChampionship
import com.example.ui.theme.Zinc100
import com.example.ui.theme.Zinc400
import com.example.ui.theme.Zinc500
import com.example.ui.theme.Zinc700
import com.example.ui.theme.Zinc800
import com.example.ui.theme.Zinc850
import com.example.ui.theme.Zinc900
import com.example.ui.theme.Zinc950

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueScreen(
    tournament: Tournament,
    matchFilter: MatchFilter,
    isPersian: Boolean,
    onBack: () -> Unit,
    onFilterChanged: (MatchFilter) -> Unit,
    onSaveMatchResult: (String, List<com.example.data.model.SetScore>) -> Unit,
    onDeleteTournament: () -> Unit
) {
    BackHandler { onBack() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var scoringMatch by remember { mutableStateOf<MatchItem?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val standings = remember(tournament.matches) {
        tournament.calculateStandings()
    }

    val playedCount = tournament.matches.count { it.isPlayed }
    val totalCount = tournament.matches.size
    val progressPercent = if (totalCount > 0) (playedCount * 100) / totalCount else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = tournament.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isPersian)
                                "${tournament.players.size} بازیکن | بهترین از ${tournament.maxSets} ست | $progressPercent٪ تکمیل شده"
                            else
                                "${tournament.players.size} players | Best of ${tournament.maxSets} sets | $progressPercent% done",
                            style = MaterialTheme.typography.bodySmall,
                            color = Zinc400
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PureWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete League",
                            tint = RoseLoss.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Zinc900
                )
            )
        },
        containerColor = Zinc950
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs Row
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Zinc900,
                contentColor = PureWhite,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                        color = PureWhite,
                        height = 3.dp
                    )
                },
                divider = { HorizontalDivider(color = Zinc800) }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = if (isPersian) "مسابقات ($playedCount/$totalCount)" else "Matches ($playedCount/$totalCount)",
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 0) PureWhite else Zinc400,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = if (isPersian) "جدول رده‌بندی 🏆" else "Standings 🏆",
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 1) PureWhite else Zinc400,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = {
                        Text(
                            text = if (isPersian) "آمار و رکوردها 📊" else "Statistics 📊",
                            fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 2) PureWhite else Zinc400,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            when (selectedTabIndex) {
                0 -> MatchesTabContent(
                    matches = tournament.matches,
                    filter = matchFilter,
                    isPersian = isPersian,
                    onFilterChanged = onFilterChanged,
                    onMatchClick = { scoringMatch = it }
                )
                1 -> StandingsTabContent(
                    standings = standings,
                    isPersian = isPersian
                )
                2 -> StatisticsTabContent(
                    tournament = tournament,
                    standings = standings,
                    isPersian = isPersian
                )
            }
        }
    }

    // Match Scoring Dialog
    scoringMatch?.let { match ->
        MatchScoringDialog(
            match = match,
            maxSets = tournament.maxSets,
            isPersian = isPersian,
            onDismiss = { scoringMatch = null },
            onSave = { sets ->
                onSaveMatchResult(match.id, sets)
                scoringMatch = null
            }
        )
    }

    // Delete League Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = if (isPersian) "حذف لیگ؟" else "Delete League?",
                    color = PureWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isPersian)
                        "آیا از حذف لیگ «${tournament.title}» و تمام نتایج آن مطمئن هستید؟"
                    else
                        "Are you sure you want to delete '${tournament.title}' and all its matches?",
                    color = Zinc400
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteTournament()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseLoss)
                ) {
                    Text(if (isPersian) "بله، حذف کن" else "Delete", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(if (isPersian) "انصراف" else "Cancel", color = Zinc400)
                }
            },
            containerColor = Zinc900
        )
    }
}

@Composable
private fun MatchesTabContent(
    matches: List<MatchItem>,
    filter: MatchFilter,
    isPersian: Boolean,
    onFilterChanged: (MatchFilter) -> Unit,
    onMatchClick: (MatchItem) -> Unit
) {
    val filteredMatches = remember(matches, filter) {
        val list = when (filter) {
            MatchFilter.ALL -> matches
            MatchFilter.PENDING -> matches.filter { !it.isPlayed }
            MatchFilter.FINISHED -> matches.filter { it.isPlayed }
        }
        // Unplayed matches at top if in ALL view
        list.sortedWith(
            compareBy<MatchItem> { it.isPlayed }
                .thenBy { it.round }
                .thenBy { it.matchIndex }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filter == MatchFilter.ALL,
                onClick = { onFilterChanged(MatchFilter.ALL) },
                label = { Text(if (isPersian) "همه (${matches.size})" else "All (${matches.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PureWhite,
                    selectedLabelColor = Zinc950,
                    containerColor = Zinc900,
                    labelColor = Zinc400
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Zinc800,
                    selectedBorderColor = PureWhite,
                    enabled = true,
                    selected = filter == MatchFilter.ALL
                )
            )

            FilterChip(
                selected = filter == MatchFilter.PENDING,
                onClick = { onFilterChanged(MatchFilter.PENDING) },
                label = {
                    Text(
                        if (isPersian) "در انتظار (${matches.count { !it.isPlayed }})"
                        else "Pending (${matches.count { !it.isPlayed }})"
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Emerald500,
                    selectedLabelColor = PureWhite,
                    containerColor = Zinc900,
                    labelColor = Zinc400
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Zinc800,
                    selectedBorderColor = Emerald500,
                    enabled = true,
                    selected = filter == MatchFilter.PENDING
                )
            )

            FilterChip(
                selected = filter == MatchFilter.FINISHED,
                onClick = { onFilterChanged(MatchFilter.FINISHED) },
                label = {
                    Text(
                        if (isPersian) "پایان یافته (${matches.count { it.isPlayed }})"
                        else "Finished (${matches.count { it.isPlayed }})"
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Zinc700,
                    selectedLabelColor = PureWhite,
                    containerColor = Zinc900,
                    labelColor = Zinc400
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Zinc800,
                    selectedBorderColor = Zinc700,
                    enabled = true,
                    selected = filter == MatchFilter.FINISHED
                )
            )
        }

        if (filteredMatches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isPersian) "هیچ مسابقه‌ای در این دسته‌بندی یافت نشد." else "No matches found in this category.",
                    color = Zinc500,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredMatches, key = { it.id }) { match ->
                    MatchCard(
                        match = match,
                        isPersian = isPersian,
                        onClick = { onMatchClick(match) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchCard(
    match: MatchItem,
    isPersian: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("match_card_${match.matchIndex}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (match.isPlayed) Zinc950.copy(alpha = 0.6f) else Zinc900
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (match.isPlayed) Zinc800 else Zinc700
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Round and status pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isPersian) "دور ${match.round}" else "Round ${match.round}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Zinc400,
                        fontFamily = JetBrainsMono
                    )
                    StatusPill(isPlayed = match.isPlayed, isPersian = isPersian)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Players matchup
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = match.player1,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (match.winner == match.player1) Emerald400 else if (match.isPlayed) Zinc400 else PureWhite
                    )

                    Text(
                        text = "vs",
                        style = MaterialTheme.typography.bodySmall,
                        color = Zinc600Color()
                    )

                    Text(
                        text = match.player2,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (match.winner == match.player2) Emerald400 else if (match.isPlayed) Zinc400 else PureWhite
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Sets Score Preview
                if (match.isPlayed && match.sets.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        match.sets.forEach { set ->
                            ScoreChip(score1 = set.score1, score2 = set.score2)
                        }
                    }
                } else {
                    Text(
                        text = if (isPersian) "⏳ در انتظار برگزاری بازی" else "⏳ Waiting to be played",
                        fontSize = 11.sp,
                        color = Zinc500
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (match.isPlayed) Zinc800 else PureWhite,
                    contentColor = if (match.isPlayed) Zinc100 else Zinc950
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (match.isPlayed) {
                        if (isPersian) "ویرایش" else "Edit"
                    } else {
                        if (isPersian) "ثبت نتیجه 🎯" else "Record 🎯"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StandingsTabContent(
    standings: List<PlayerStanding>,
    isPersian: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Podium section for top 3
        if (standings.size >= 2) {
            item {
                PodiumSection(standings = standings.take(3), isPersian = isPersian)
            }
        }

        item {
            Text(
                text = if (isPersian) "جدول رده‌بندی دقیق" else "Full Standings Table",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Zinc400
            )
        }

        // Table Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Zinc900)
                    .border(1.dp, Zinc800, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isPersian) "رتبه" else "Rank",
                    style = MaterialTheme.typography.labelSmall,
                    color = Zinc400,
                    modifier = Modifier.width(36.dp)
                )
                Text(
                    text = if (isPersian) "بازیکن" else "Player",
                    style = MaterialTheme.typography.labelSmall,
                    color = Zinc400,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (isPersian) "بازی" else "P",
                    style = MaterialTheme.typography.labelSmall,
                    color = Zinc400,
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (isPersian) "برد" else "W",
                    style = MaterialTheme.typography.labelSmall,
                    color = Zinc400,
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (isPersian) "تفاضل" else "SD",
                    style = MaterialTheme.typography.labelSmall,
                    color = Zinc400,
                    modifier = Modifier.width(42.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (isPersian) "امتیاز" else "Pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(42.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Table Rows
        items(standings) { standing ->
            val setDiffStr = if (standing.setDiff > 0) "+${standing.setDiff}" else "${standing.setDiff}"

            val rowBg = when (standing.rank) {
                1 -> AmberGold.copy(alpha = 0.08f)
                else -> Zinc900
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(rowBg)
                    .border(1.dp, Zinc800)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${standing.rank}",
                    fontFamily = JetBrainsMono,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (standing.rank) {
                        1 -> AmberGold
                        2 -> SilverChampionship
                        3 -> BronzeChampionship
                        else -> Zinc400
                    },
                    modifier = Modifier.width(36.dp)
                )

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayerAvatar(name = standing.name, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = standing.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PureWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "${standing.played}",
                    fontFamily = JetBrainsMono,
                    fontSize = 13.sp,
                    color = Zinc400,
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "${standing.wins}",
                    fontFamily = JetBrainsMono,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Emerald400,
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = setDiffStr,
                    fontFamily = JetBrainsMono,
                    fontSize = 13.sp,
                    color = if (standing.setDiff > 0) Emerald400 else if (standing.setDiff < 0) RoseLoss else Zinc400,
                    modifier = Modifier.width(42.dp),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "${standing.points}",
                    fontFamily = JetBrainsMono,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                    modifier = Modifier.width(42.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PodiumSection(
    standings: List<PlayerStanding>,
    isPersian: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Zinc900),
        border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isPersian) "🏆 صدرنشینان تورنمنت" else "🏆 Tournament Leaders",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd Place (Silver)
                if (standings.size >= 2) {
                    PodiumPillar(
                        rank = 2,
                        standing = standings[1],
                        heightDp = 80,
                        color = SilverChampionship,
                        icon = "🥈"
                    )
                }

                // 1st Place (Gold)
                if (standings.isNotEmpty()) {
                    PodiumPillar(
                        rank = 1,
                        standing = standings[0],
                        heightDp = 106,
                        color = AmberGold,
                        icon = "🥇"
                    )
                }

                // 3rd Place (Bronze)
                if (standings.size >= 3) {
                    PodiumPillar(
                        rank = 3,
                        standing = standings[2],
                        heightDp = 64,
                        color = BronzeChampionship,
                        icon = "🥉"
                    )
                }
            }
        }
    }
}

@Composable
private fun PodiumPillar(
    rank: Int,
    standing: PlayerStanding,
    heightDp: Int,
    color: Color,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Text(
            text = standing.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = PureWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${standing.points} pts",
            fontFamily = JetBrainsMono,
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .width(76.dp)
                .height(heightDp.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(color.copy(alpha = 0.2f))
                .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                fontFamily = JetBrainsMono,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun StatisticsTabContent(
    tournament: Tournament,
    standings: List<PlayerStanding>,
    isPersian: Boolean
) {
    val totalMatches = tournament.matches.size
    val playedMatches = tournament.matches.count { it.isPlayed }
    val totalSetsPlayed = tournament.matches.filter { it.isPlayed }.sumOf { it.sets.size }
    val totalPointsScored = tournament.matches.filter { it.isPlayed }
        .sumOf { it.sets.sumOf { s -> s.score1 + s.score2 } }

    val topScorer = standings.maxByOrNull { it.pointDiff }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = if (isPersian) "آمار کلی مسابقات" else "Tournament Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = if (isPersian) "بازی‌های انجام شده" else "Matches Played",
                    value = "$playedMatches / $totalMatches",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (isPersian) "کل ست‌های برگزار شده" else "Total Sets Played",
                    value = "$totalSetsPlayed",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = if (isPersian) "مجموع امتیازات زده" else "Total Points Scored",
                    value = "$totalPointsScored",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (isPersian) "بهترین تفاضل امتیاز" else "Best Point Diff",
                    value = topScorer?.let { "${it.name} (+${it.pointDiff})" } ?: "-",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Zinc900),
                border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isPersian) "قوانین و فرمت امتیازدهی" else "Scoring Rules & Format",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isPersian)
                            "• هر برد ۲ امتیاز و هر باخت ۰ امتیاز در جدول دارد.\n" +
                            "• رده‌بندی به ترتیب: ۱) امتیاز ۲) تفاضل ست ۳) تفاضل امتیاز ۴) تعداد برد.\n" +
                            "• هر مسابقه بر اساس بهترین از ${tournament.maxSets} ست برگزار می‌شود."
                        else
                            "• 2 points for a win, 0 points for a loss.\n" +
                            "• Ranking order: 1) Points 2) Set Diff 3) Point Diff 4) Total Wins.\n" +
                            "• Matches played in best-of-${tournament.maxSets} sets format.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Zinc400,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Zinc900),
        border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Zinc400
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = JetBrainsMono,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
        }
    }
}

@Composable
private fun Zinc600Color(): Color = Color(0xFF52525B)
