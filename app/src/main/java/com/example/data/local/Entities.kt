package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val playersJson: String,
    val maxSets: Int,
    val createdAt: Long
)

@Entity(
    tableName = "matches",
    foreignKeys = [
        ForeignKey(
            entity = TournamentEntity::class,
            parentColumns = ["id"],
            childColumns = ["tournamentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tournamentId"])]
)
data class MatchEntity(
    @PrimaryKey val id: String,
    val tournamentId: String,
    val matchIndex: Int,
    val round: Int,
    val player1: String,
    val player2: String,
    val setsJson: String,
    val isPlayed: Boolean
)
