package com.example.data.model

data class SetScore(
    val score1: Int,
    val score2: Int
)

data class MatchItem(
    val id: String,
    val tournamentId: String,
    val matchIndex: Int,
    val round: Int,
    val player1: String,
    val player2: String,
    val sets: List<SetScore> = emptyList(),
    val isPlayed: Boolean = false
) {
    val player1SetsWon: Int
        get() = sets.count { it.score1 > it.score2 }

    val player2SetsWon: Int
        get() = sets.count { it.score2 > it.score1 }

    val winner: String?
        get() = when {
            !isPlayed -> null
            player1SetsWon > player2SetsWon -> player1
            player2SetsWon > player1SetsWon -> player2
            else -> null
        }
}

data class PlayerStanding(
    val rank: Int = 0,
    val name: String,
    val played: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val setDiff: Int = 0,
    val pointDiff: Int = 0,
    val points: Int = 0
)

data class Tournament(
    val id: String,
    val title: String,
    val players: List<String>,
    val maxSets: Int,
    val createdAt: Long,
    val matches: List<MatchItem> = emptyList()
) {
    fun calculateStandings(): List<PlayerStanding> {
        val stats = players.associateWith { name ->
            PlayerStats(name = name)
        }.toMutableMap()

        for (m in matches) {
            if (m.isPlayed && m.sets.isNotEmpty()) {
                val p1 = stats[m.player1]
                val p2 = stats[m.player2]
                if (p1 != null && p2 != null) {
                    var p1SetWins = 0
                    var p2SetWins = 0

                    for (s in m.sets) {
                        p1.pointDiff += (s.score1 - s.score2)
                        p2.pointDiff += (s.score2 - s.score1)

                        if (s.score1 > s.score2) p1SetWins++
                        else if (s.score2 > s.score1) p2SetWins++
                    }

                    p1.played++
                    p2.played++

                    p1.setDiff += (p1SetWins - p2SetWins)
                    p2.setDiff += (p2SetWins - p1SetWins)

                    if (p1SetWins > p2SetWins) {
                        p1.wins++
                        p1.points += 2
                        p2.losses++
                    } else if (p2SetWins > p1SetWins) {
                        p2.wins++
                        p2.points += 2
                        p1.losses++
                    }
                }
            }
        }

        val sorted = stats.values.toList().sortedWith(
            compareByDescending<PlayerStats> { it.points }
                .thenByDescending { it.setDiff }
                .thenByDescending { it.pointDiff }
                .thenByDescending { it.wins }
        )

        return sorted.mapIndexed { index, stat ->
            PlayerStanding(
                rank = index + 1,
                name = stat.name,
                played = stat.played,
                wins = stat.wins,
                losses = stat.losses,
                setDiff = stat.setDiff,
                pointDiff = stat.pointDiff,
                points = stat.points
            )
        }
    }
}

private class PlayerStats(
    val name: String,
    var played: Int = 0,
    var wins: Int = 0,
    var losses: Int = 0,
    var setDiff: Int = 0,
    var pointDiff: Int = 0,
    var points: Int = 0
)
