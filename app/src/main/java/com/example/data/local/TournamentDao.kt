package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {

    @Query("SELECT * FROM tournaments ORDER BY createdAt DESC")
    fun getAllTournaments(): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE id = :tournamentId LIMIT 1")
    suspend fun getTournamentById(tournamentId: String): TournamentEntity?

    @Query("SELECT * FROM matches WHERE tournamentId = :tournamentId ORDER BY matchIndex ASC")
    fun getMatchesForTournament(tournamentId: String): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE tournamentId = :tournamentId ORDER BY matchIndex ASC")
    suspend fun getMatchesListSync(tournamentId: String): List<MatchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: TournamentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Query("UPDATE matches SET setsJson = :setsJson, isPlayed = :isPlayed WHERE id = :matchId")
    suspend fun updateMatchResult(matchId: String, setsJson: String, isPlayed: Boolean)

    @Query("DELETE FROM tournaments WHERE id = :tournamentId")
    suspend fun deleteTournament(tournamentId: String)

    @Transaction
    suspend fun insertTournamentWithMatches(tournament: TournamentEntity, matches: List<MatchEntity>) {
        insertTournament(tournament)
        insertMatches(matches)
    }
}
