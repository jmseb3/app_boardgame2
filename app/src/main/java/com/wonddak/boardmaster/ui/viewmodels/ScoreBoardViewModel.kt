package com.wonddak.boardmaster.ui.viewmodels

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wonddak.boardmaster.room.AppDatabase
import com.wonddak.boardmaster.room.StartGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.reflect.typeOf

class ScoreBoardViewModel(application: Application) : AndroidViewModel(application) {
    val db = AppDatabase.getInstance(application)
    val prefs: SharedPreferences = application.getSharedPreferences("boardgame", 0)
    var iddata = prefs.getInt("iddata", 0)

    var board_map: MutableMap<Int, IntArray> = mutableMapOf()
    var sum_socre: MutableLiveData<IntArray> = MutableLiveData()
    var maxValueIDX: MutableLiveData<List<Int>> = MutableLiveData()
    var wait = true

    init {
        maxValueIDX.value = mutableListOf()
    }


    fun getPerson(): List<String> {
        return db.dataDao().getPersonNameByGameId(iddata)
    }
    fun getGame(): String {
        return db.dataDao().getGameNameById(iddata)
    }

    fun startBoardmap() {
        GlobalScope.launch(Dispatchers.IO) {
            val temp = db.dataDao().getGameScoreById(iddata)
            if (temp == "") {
                board_map[1] = IntArray(getPerson().size) { 0 }
            } else {
                wait = false
                val temp2 = stringtomap(temp)
                for ((x, row) in temp2.entries) {
                    board_map[x] = toIntArray(row)!!
                }
                updateSumScore()
            }
        }

    }

    fun updateBoardmap(map: Map<Int, IntArray>) {
        GlobalScope.launch(Dispatchers.IO) {
            val temp = mapToString(map)
            db.dataDao().UpdateGameScoreById(iddata, temp)
        }
    }

    fun updateMaxValueIdx(value: List<Int>) {
        maxValueIDX.value = value
    }

    fun updateSumScore() {
        GlobalScope.launch(Dispatchers.IO) {
            val personList = getPerson()
            val temp = IntArray(personList.size) { 0 }
            for (idx in personList.indices) {
                for (row in board_map.values) {
                    temp[idx] += row[idx]
                }
            }
            launch(Dispatchers.Main) {
                sum_socre.value = temp
            }
        }
    }

    fun stringtomap(string: String): MutableMap<Int, List<Int>> {
        return Gson().fromJson(string, object : TypeToken<Map<Int, List<Int>>>() {}.type)
    }

    fun mapToString(map: Map<Int, IntArray>): String {
        return Gson().toJson(map)
    }

    fun toIntArray(list: List<Int>): IntArray? {
        val ret = IntArray(list.size)
        for (i in ret.indices) ret[i] = list[i]
        return ret
    }


}