package com.wonddak.boardmaster.room

import androidx.room.*


@Entity
data class StartGame(
    @PrimaryKey(autoGenerate = true) val gameId: Int?,
    val gameName: String,
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
    val personName: String,
    val scoreList : List<String>
)

data class StartGameWithPersonList(
    @Embedded val StartGame :StartGame,
    @Relation(
        parentColumn = "gameId",
        entityColumn = "InGameId",
    )
    val PersonList: List<PersonList>
)

class Converters {
//    문자열을 리스트로 반환
    @TypeConverter
    fun toListOfStrings(flatStringList: String): List<String> {
        return flatStringList.split(",")
    }
    @TypeConverter
//    리스트를 문자열로 반환
    fun fromListOfStrings(listOfString: List<String>): String {
        return listOfString.joinToString(",")
    }
}


