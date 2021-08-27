package com.wonddak.boardmaster.room

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@Entity
data class StartGame(
    @PrimaryKey(autoGenerate = true) val gameId: Int?,
    val gameName: String,
    val scoreList: String
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = StartGame::class,
            parentColumns = arrayOf("gameId"),
            childColumns = arrayOf("InGameId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PersonList(
    @PrimaryKey(autoGenerate = true) val personId: Int?,
    val InGameId: Int,
    val personName: String
)

data class StartGameWithPersonList(
    @Embedded val StartGame: StartGame,
    @Relation(
        parentColumn = "gameId",
        entityColumn = "InGameId",
    )
    val PersonList: List<PersonList>
)


data class ScoreBoard(
    val round: Int,
    val scorelist: IntArray
)