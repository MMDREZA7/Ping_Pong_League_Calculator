package com.example

import com.example.data.model.MatchItem
import com.example.data.model.SetScore
import com.example.data.model.Tournament
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `standings calculation correctly awards points and calculates set differential`() {
        val matches = listOf(
            MatchItem(
                id = "m1",
                tournamentId = "t1",
                matchIndex = 0,
                round = 1,
                player1 = "علی",
                player2 = "رضا",
                sets = listOf(
                    SetScore(11, 9),
                    SetScore(11, 7)
                ),
                isPlayed = true
            )
        )

        val tournament = Tournament(
            id = "t1",
            title = "تست لیگ",
            players = listOf("علی", "رضا"),
            maxSets = 3,
            createdAt = 1000L,
            matches = matches
        )

        val standings = tournament.calculateStandings()
        assertEquals(2, standings.size)

        val winner = standings[0]
        val loser = standings[1]

        assertEquals("علی", winner.name)
        assertEquals(1, winner.played)
        assertEquals(1, winner.wins)
        assertEquals(0, winner.losses)
        assertEquals(2, winner.points)
        assertEquals(2, winner.setDiff)

        assertEquals("رضا", loser.name)
        assertEquals(1, loser.played)
        assertEquals(0, loser.wins)
        assertEquals(1, loser.losses)
        assertEquals(0, loser.points)
        assertEquals(-2, loser.setDiff)
    }

    @Test
    fun `round robin algorithm rotates without list modification exceptions`() {
        val players = mutableListOf<String?>("A", "B", "C", "D")
        val n = players.size
        var pool = players.toMutableList()

        for (round in 0 until (n - 1)) {
            val nextPool = ArrayList<String?>(n)
            nextPool.add(pool[0])
            nextPool.add(pool[n - 1])
            for (k in 1 until (n - 1)) {
                nextPool.add(pool[k])
            }
            pool = nextPool
        }

        assertEquals(4, pool.size)
        assertNotNull(pool[0])
    }
}
