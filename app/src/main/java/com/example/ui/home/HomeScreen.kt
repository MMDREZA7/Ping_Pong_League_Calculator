package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Tournament
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoseLoss
import com.example.ui.theme.Zinc100
import com.example.ui.theme.Zinc300
import com.example.ui.theme.Zinc400
import com.example.ui.theme.Zinc500
import com.example.ui.theme.Zinc700
import com.example.ui.theme.Zinc800
import com.example.ui.theme.Zinc850
import com.example.ui.theme.Zinc900
import com.example.ui.theme.Zinc950
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    tournaments: List<Tournament>,
    isPersian: Boolean,
    onToggleLanguage: () -> Unit,
    onSelectTournament: (String) -> Unit,
    onCreateTournament: (String, List<String>, Int) -> Unit,
    onDeleteTournament: (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var isCreateExpanded by remember { mutableStateOf(true) }
    var titleText by remember { mutableStateOf("") }
    var playerCount by remember { mutableIntStateOf(4) }
    var maxSets by remember { mutableIntStateOf(3) }
    var setsDropdownExpanded by remember { mutableStateOf(false) }

    val playerNames = remember {
        mutableStateListOf("", "", "", "")
    }

    fun updatePlayerCount(newCount: Int) {
        if (newCount in 2..16) {
            playerCount = newCount
            while (playerNames.size < newCount) {
                playerNames.add("")
            }
            while (playerNames.size > newCount) {
                playerNames.removeAt(playerNames.size - 1)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ping_pong_logo_1794298657844),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isPersian) "مدیریت لیگ‌های پینگ‌پنگ" else "Ping Pong League",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                            Text(
                                text = if (isPersian) "سیستم قرعه‌کشی و جدول رده‌بندی" else "Tournament & Standings System",
                                style = MaterialTheme.typography.bodySmall,
                                color = Zinc400,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    // Language switcher button
                    TextButton(
                        onClick = onToggleLanguage,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Toggle Language",
                            tint = Emerald400,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPersian) "FA / EN" else "EN / FA",
                            color = Emerald400,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Zinc900)
            )
        },
        containerColor = Zinc950
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Statistics Header Banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryStatCard(
                        title = if (isPersian) "لیگ‌های فعال" else "Active Leagues",
                        value = "${tournaments.size}",
                        icon = "🏆",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        title = if (isPersian) "کل بازیکنان" else "Total Players",
                        value = "${tournaments.sumOf { it.players.size }}",
                        icon = "🏓",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Create New League Expandable Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_league_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Zinc900),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isCreateExpanded = !isCreateExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Emerald500.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SportsTennis,
                                        contentDescription = null,
                                        tint = Emerald400,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isPersian) "راه‌اندازی لیگ جدید" else "Create New League",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite
                                    )
                                    Text(
                                        text = if (isPersian) "تعیین عنوان، تعداد نفرات و ست‌ها" else "Set title, players & sets",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Zinc500
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isCreateExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Toggle Expand",
                                tint = Zinc400
                            )
                        }

                        AnimatedVisibility(
                            visible = isCreateExpanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(modifier = Modifier.padding(top = 16.dp)) {
                                // Tournament Title Input
                                OutlinedTextField(
                                    value = titleText,
                                    onValueChange = { titleText = it },
                                    label = { Text(if (isPersian) "عنوان لیگ / تورنمنت" else "League / Tournament Title") },
                                    placeholder = { Text(if (isPersian) "مثلاً: جام دوستانه آدینه" else "e.g. Friday Friendly Cup") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("tournament_title_input"),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Zinc950,
                                        unfocusedContainerColor = Zinc950,
                                        focusedBorderColor = PureWhite,
                                        unfocusedBorderColor = Zinc800,
                                        focusedTextColor = PureWhite,
                                        unfocusedTextColor = Zinc100,
                                        focusedLabelColor = PureWhite,
                                        unfocusedLabelColor = Zinc400
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Players Count & Max Sets
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Player count counter
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isPersian) "تعداد بازیکنان" else "Players Count",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Zinc400
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(52.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Zinc950)
                                                .border(1.dp, Zinc800, RoundedCornerShape(12.dp))
                                                .padding(horizontal = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            IconButton(
                                                onClick = { updatePlayerCount(playerCount - 1) },
                                                enabled = playerCount > 2
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Remove,
                                                    contentDescription = "Decrease",
                                                    tint = if (playerCount > 2) Zinc300 else Zinc700,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            Text(
                                                text = if (isPersian) "$playerCount نفر" else "$playerCount players",
                                                fontFamily = JetBrainsMono,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = PureWhite
                                            )

                                            IconButton(
                                                onClick = { updatePlayerCount(playerCount + 1) },
                                                enabled = playerCount < 16
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Increase",
                                                    tint = if (playerCount < 16) Zinc300 else Zinc700,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Max Sets Selector
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isPersian) "ست‌های هر مسابقه" else "Sets per Match",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Zinc400
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))

                                        ExposedDropdownMenuBox(
                                            expanded = setsDropdownExpanded,
                                            onExpandedChange = { setsDropdownExpanded = it }
                                        ) {
                                            OutlinedTextField(
                                                value = if (isPersian) "بهترین از $maxSets ست" else "Best of $maxSets",
                                                onValueChange = {},
                                                readOnly = true,
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = setsDropdownExpanded) },
                                                modifier = Modifier
                                                    .menuAnchor()
                                                    .fillMaxWidth()
                                                    .height(52.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedContainerColor = Zinc950,
                                                    unfocusedContainerColor = Zinc950,
                                                    focusedBorderColor = PureWhite,
                                                    unfocusedBorderColor = Zinc800,
                                                    focusedTextColor = PureWhite,
                                                    unfocusedTextColor = Zinc100
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            )

                                            ExposedDropdownMenu(
                                                expanded = setsDropdownExpanded,
                                                onDismissRequest = { setsDropdownExpanded = false },
                                                modifier = Modifier.background(Zinc900)
                                            ) {
                                                listOf(1, 2, 3, 5, 7).forEach { sets ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(
                                                                text = if (isPersian) "بهترین از $sets ست" else "Best of $sets sets",
                                                                color = PureWhite
                                                            )
                                                        },
                                                        onClick = {
                                                            maxSets = sets
                                                            setsDropdownExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Dynamic Player Name Fields
                                Text(
                                    text = if (isPersian) "نام بازیکنان (با Enter به بعدی بروید)" else "Player Names (Press Enter to advance)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Zinc400
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                 val chunkedPlayers = playerNames.indices.chunked(2)
                                chunkedPlayers.forEach { rowIndices ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowIndices.forEach { idx ->
                                            val playerLabel = if (isPersian) "بازیکن ${idx + 1}" else "Player ${idx + 1}"
                                            OutlinedTextField(
                                                value = playerNames[idx],
                                                onValueChange = { playerNames[idx] = it },
                                                label = { Text(playerLabel) },
                                                placeholder = { Text(playerLabel, color = Zinc500) },
                                                singleLine = true,
                                                modifier = Modifier.weight(1f),
                                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                                keyboardActions = KeyboardActions(
                                                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                                                ),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedContainerColor = Zinc950,
                                                    unfocusedContainerColor = Zinc950,
                                                    focusedBorderColor = Emerald400,
                                                    unfocusedBorderColor = Zinc800,
                                                    focusedTextColor = PureWhite,
                                                    unfocusedTextColor = Zinc100,
                                                    focusedLabelColor = Emerald400,
                                                    unfocusedLabelColor = Zinc400
                                                ),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                        }
                                        if (rowIndices.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Create Tournament Button
                                Button(
                                    onClick = {
                                        val title = titleText.trim()
                                        val validPlayers = playerNames.mapIndexed { idx, name ->
                                            val trimmed = name.trim()
                                            if (trimmed.isNotEmpty()) trimmed else (if (isPersian) "بازیکن ${idx + 1}" else "Player ${idx + 1}")
                                        }

                                        if (title.isEmpty()) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    if (isPersian) "لطفاً عنوان لیگ را وارد کنید." else "Please enter tournament title."
                                                )
                                            }
                                            return@Button
                                        }

                                        if (validPlayers.size < 2) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    if (isPersian) "حداقل ۲ بازیکن با نام معتبر وارد کنید." else "Please enter at least 2 players."
                                                )
                                            }
                                            return@Button
                                        }

                                        onCreateTournament(title, validPlayers, maxSets)
                                        titleText = ""
                                        for (i in playerNames.indices) {
                                            playerNames[i] = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("create_tournament_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PureWhite,
                                        contentColor = Zinc950
                                    )
                                ) {
                                    Text(
                                        text = if (isPersian) "قرعه‌کشی و ثبت لیگ جدید 🚀" else "Draw Lots & Start League 🚀",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Saved Tournaments Header
            item {
                Text(
                    text = if (isPersian) "لیست تمام لیگ‌های ذخیره‌شده" else "Saved Tournaments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Zinc400,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Saved Tournaments List
            if (tournaments.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Zinc900),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🏓", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isPersian) "هنوز هیچ لیگی ثبت نشده است." else "No tournaments registered yet.",
                                style = MaterialTheme.typography.titleSmall,
                                color = Zinc400
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isPersian) "از فرم بالا یک لیگ جدید بسازید و بازی‌ها را آغاز کنید!" else "Create a tournament above to get started!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Zinc500
                            )
                        }
                    }
                }
            } else {
                items(tournaments, key = { it.id }) { tourney ->
                    TournamentCard(
                        tournament = tourney,
                        isPersian = isPersian,
                        onClick = { onSelectTournament(tourney.id) },
                        onDelete = { onDeleteTournament(tourney.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TournamentCard(
    tournament: Tournament,
    isPersian: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val playedCount = tournament.matches.count { it.isPlayed }
    val totalMatches = tournament.matches.size
    val progress = if (totalMatches > 0) playedCount.toFloat() / totalMatches else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("tournament_card_${tournament.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Zinc900),
        border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tournament.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isPersian)
                            "${tournament.players.size} بازیکن | بهترین از ${tournament.maxSets} ست"
                        else
                            "${tournament.players.size} players | Best of ${tournament.maxSets} sets",
                        style = MaterialTheme.typography.bodySmall,
                        color = Zinc400
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var showDeleteDialog by remember { mutableStateOf(false) }

                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = RoseLoss.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (showDeleteDialog) {
                        androidx.compose.material3.AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = {
                                Text(
                                    text = if (isPersian) "حذف لیگ" else "Delete League",
                                    color = PureWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Text(
                                    text = if (isPersian)
                                        "آیا از حذف «${tournament.title}» مطمئن هستید؟"
                                    else
                                        "Are you sure you want to delete '${tournament.title}'?",
                                    color = Zinc400
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showDeleteDialog = false
                                        onDelete()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoseLoss)
                                ) {
                                    Text(if (isPersian) "حذف" else "Delete", color = PureWhite)
                                }
                            },
                            dismissButton = {
                                androidx.compose.material3.TextButton(onClick = { showDeleteDialog = false }) {
                                    Text(if (isPersian) "انصراف" else "Cancel", color = Zinc400)
                                }
                            },
                            containerColor = Zinc900
                        )
                    }

                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PureWhite,
                            contentColor = Zinc950
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isPersian) "ورود 🏆" else "Enter 🏆",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Match progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isPersian)
                        "پیشرفت مسابقات: $playedCount از $totalMatches بازی"
                    else
                        "Progress: $playedCount of $totalMatches played",
                    fontSize = 11.sp,
                    color = Zinc500
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald400
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Emerald500,
                trackColor = Zinc800,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun SummaryStatCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Zinc900),
        border = androidx.compose.foundation.BorderStroke(1.dp, Zinc800)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = Zinc400
                )
                Text(
                    text = value,
                    fontFamily = JetBrainsMono,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }
        }
    }
}
