package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.TournamentViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.league.LeagueScreen
import com.example.ui.theme.PingPongTheme
import com.example.ui.theme.Zinc950

class MainActivity : ComponentActivity() {

    private val viewModel: TournamentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val tournaments by viewModel.tournaments.collectAsState()
            val currentTournament by viewModel.currentTournament.collectAsState()
            val selectedTournamentId by viewModel.selectedTournamentId.collectAsState()
            val matchFilter by viewModel.matchFilter.collectAsState()
            val isPersian by viewModel.isPersianLanguage.collectAsState()

            // Clear any legacy default tournament if it was stored previously
            LaunchedEffect(tournaments) {
                val legacyDefault = tournaments.filter { it.title == "جام دوستانه آدینه" }
                legacyDefault.forEach { viewModel.deleteTournament(it.id) }
            }

            PingPongTheme(darkTheme = true) {
                val layoutDirection = if (isPersian) LayoutDirection.Rtl else LayoutDirection.Ltr

                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Zinc950
                    ) {
                        AnimatedContent(
                            targetState = selectedTournamentId != null && currentTournament != null,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "ScreenTransition"
                        ) { isLeagueDetail ->
                            if (isLeagueDetail && currentTournament != null) {
                                LeagueScreen(
                                    tournament = currentTournament!!,
                                    matchFilter = matchFilter,
                                    isPersian = isPersian,
                                    onBack = { viewModel.selectTournament(null) },
                                    onFilterChanged = { viewModel.setMatchFilter(it) },
                                    onSaveMatchResult = { matchId, sets ->
                                        viewModel.updateMatchResult(matchId, sets)
                                    },
                                    onDeleteTournament = {
                                        currentTournament?.let { viewModel.deleteTournament(it.id) }
                                    }
                                )
                            } else {
                                HomeScreen(
                                    tournaments = tournaments,
                                    isPersian = isPersian,
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onSelectTournament = { id -> viewModel.selectTournament(id) },
                                    onCreateTournament = { title, players, maxSets ->
                                        viewModel.createTournament(title, players, maxSets, autoSelect = true)
                                    },
                                    onDeleteTournament = { id -> viewModel.deleteTournament(id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
