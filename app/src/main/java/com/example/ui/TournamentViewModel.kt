package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.MatchItem
import com.example.data.model.SetScore
import com.example.data.model.Tournament
import com.example.data.repository.TournamentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MatchFilter {
    ALL, PENDING, FINISHED
}

class TournamentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TournamentRepository = TournamentRepository(
        AppDatabase.getDatabase(application).tournamentDao()
    )

    val tournaments: StateFlow<List<Tournament>> = repository.allTournaments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTournamentId = MutableStateFlow<String?>(null)
    val selectedTournamentId: StateFlow<String?> = _selectedTournamentId.asStateFlow()

    val currentTournament: StateFlow<Tournament?> = _selectedTournamentId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getTournamentWithMatches(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _matchFilter = MutableStateFlow(MatchFilter.ALL)
    val matchFilter: StateFlow<MatchFilter> = _matchFilter.asStateFlow()

    private val _isPersianLanguage = MutableStateFlow(true)
    val isPersianLanguage: StateFlow<Boolean> = _isPersianLanguage.asStateFlow()

    fun toggleLanguage() {
        _isPersianLanguage.value = !_isPersianLanguage.value
    }

    fun selectTournament(tournamentId: String?) {
        _selectedTournamentId.value = tournamentId
    }

    fun setMatchFilter(filter: MatchFilter) {
        _matchFilter.value = filter
    }

    fun createTournament(
        title: String,
        players: List<String>,
        maxSets: Int,
        autoSelect: Boolean = true,
        onCreated: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val id = repository.createTournament(title, players, maxSets)
            if (autoSelect) {
                _selectedTournamentId.value = id
            }
            onCreated(id)
        }
    }

    fun updateMatchResult(matchId: String, sets: List<SetScore>) {
        viewModelScope.launch {
            repository.updateMatchResult(matchId, sets)
        }
    }

    fun deleteTournament(tournamentId: String) {
        viewModelScope.launch {
            if (_selectedTournamentId.value == tournamentId) {
                _selectedTournamentId.value = null
            }
            repository.deleteTournament(tournamentId)
        }
    }
}
