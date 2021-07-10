package com.wonddak.boardmaster.room

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.gson.Gson


@Dao
interface DataDao {

//    게임 이름관련

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGame(gameName: StartGame): Long

    @Query("SELECT gameName FROM StartGame WHERE gameId=:gameId")
    fun getGameNameById(gameId: Int): String


    //    사람관련
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(personname:PersonList): Long

    @Query("SELECT * FROM PersonList WHERE InGameId=:gameId")
    fun getPersonDataByGameId(gameId: Int): List<PersonList>


    @Query("SELECT * FROM StartGame WHERE gameId=:gameId")
    fun getGameNameAndPersonNByUd(gameId: Int): List<StartGameWithPersonList>

    @Query("SELECT personName FROM PersonList LIMIT 30")
    fun getPersonName(): List<String>


    @Transaction
    @Query("SELECT * FROM StartGame")
    fun getGameNameAndPerson(): List<StartGameWithPersonList>

}