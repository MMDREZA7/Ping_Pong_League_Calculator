package com.example.data.repository

import com.example.data.local.MatchEntity
import com.example.data.local.TournamentDao
import com.example.data.local.TournamentEntity
import com.example.data.model.MatchItem
import com.example.data.model.SetScore
import com.example.data.model.Tournament
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class TournamentRepository(private val dao: TournamentDao) {

    val allTournaments: Flow<List<Tournament>> = dao.getAllTournaments().map { entities ->
        entities.map { entity ->
            val players = parseStringList(entity.playersJson)
            Tournament(
                id = entity.id,
                title = entity.title,
                players = players,
                maxSets = entity.maxSets,
                createdAt = entity.createdAt,
                matches = emptyList()
            )
        }
    }

    fun getTournamentWithMatches(tournamentId: String): Flow<Tournament?> {
        return dao.getAllTournaments().combine(dao.getMatchesForTournament(tournamentId)) { tournaments, matchEntities ->
            val entity = tournaments.find { it.id == tournamentId } ?: return@combine null
            val players = parseStringList(entity.playersJson)
            val matches = matchEntities.map { m ->
                MatchItem(
                    id = m.id,
                    tournamentId = m.tournamentId,
                    matchIndex = m.matchIndex,
                    round = m.round,
                    player1 = m.player1,
                    player2 = m.player2,
                    sets = parseSets(m.setsJson),
                    isPlayed = m.isPlayed
                )
            }
            Tournament(
                id = entity.id,
                title = entity.title,
                players = players,
                maxSets = entity.maxSets,
                createdAt = entity.createdAt,
                matches = matches
            )
        }
    }

    suspend fun createTournament(title: String, players: List<String>, maxSets: Int): String {
        require(players.size >= 2) { "At least 2 players are required" }

        val tournamentId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        // Round-robin scheduling algorithm (Berger / Circle Method)
        var playerPool = players.shuffled().toMutableList<String?>()
        if (playerPool.size % 2 != 0) {
            playerPool.add(null) // Bye for odd players
        }

        val n = playerPool.size
        val matchEntities = mutableListOf<MatchEntity>()
        var matchIndex = 0

        for (round in 0 until (n - 1)) {
            for (i in 0 until (n / 2)) {
                val p1 = playerPool[i]
                val p2 = playerPool[n - 1 - i]
                if (p1 != null && p2 != null) {
                    matchEntities.add(
                        MatchEntity(
                            id = UUID.randomUUID().toString(),
                            tournamentId = tournamentId,
                            matchIndex = matchIndex++,
                            round = round + 1,
                            player1 = p1,
                            player2 = p2,
                            setsJson = "[]",
                            isPlayed = false
                        )
                    )
                }
            }
            // Rotate list elements keeping first fixed
            val nextPool = ArrayList<String?>(n)
            nextPool.add(playerPool[0])
            nextPool.add(playerPool[n - 1])
            for (k in 1 until (n - 1)) {
                nextPool.add(playerPool[k])
            }
            playerPool = nextPool
        }

        val tournamentEntity = TournamentEntity(
            id = tournamentId,
            title = title,
            playersJson = serializeStringList(players),
            maxSets = maxSets,
            createdAt = now
        )

        dao.insertTournamentWithMatches(tournamentEntity, matchEntities)
        return tournamentId
    }

    suspend fun updateMatchResult(matchId: String, sets: List<SetScore>) {
        val isPlayed = sets.isNotEmpty()
        dao.updateMatchResult(
            matchId = matchId,
            setsJson = serializeSets(sets),
            isPlayed = isPlayed
        )
    }

    suspend fun deleteTournament(tournamentId: String) {
        dao.deleteTournament(tournamentId)
    }

    // JSON Helpers
    private fun serializeStringList(list: List<String>): String {
        val array = JSONArray()
        list.forEach { array.put(it) }
        return array.toString()
    }

    private fun parseStringList(json: String): List<String> {
        val result = mutableListOf<String>()
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            result.add(array.getString(i))
        }
        return result
    }

    private fun serializeSets(sets: List<SetScore>): String {
        val array = JSONArray()
        sets.forEach { set ->
            val obj = JSONObject()
            obj.put("score1", set.score1)
            obj.put("score2", set.score2)
            array.put(obj)
        }
        return array.toString()
    }

    private fun parseSets(json: String): List<SetScore> {
        if (json.isBlank()) return emptyList()
        val result = mutableListOf<SetScore>()
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            result.add(
                SetScore(
                    score1 = obj.getInt("score1"),
                    score2 = obj.getInt("score2")
                )
            )
        }
        return result
    }
}
