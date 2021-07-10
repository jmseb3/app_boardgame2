package com.wonddak.boardmaster

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import com.wonddak.boardmaster.databinding.ActivityMainBinding
import com.wonddak.boardmaster.room.AppDatabase
import com.wonddak.boardmaster.room.PersonList
import com.wonddak.boardmaster.room.StartGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private var backKeyPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = AppDatabase.getInstance(this)
        setSupportActionBar(binding.toolbarMain)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val prefs: SharedPreferences = this.getSharedPreferences("boardgame", 0)
        var iddata = prefs.getInt("iddata", 0)


        binding.btnStartNewGameMainFrag.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val temp = db.dataDao().getPersonName().toSet().toMutableList()
                launch(Dispatchers.Main) {
                    supportFragmentManager
                        .beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.frag_area, GameSettingFragment().apply {
                            arguments = Bundle().apply {
                                putStringArrayList("exist",temp as ArrayList<String>)
                            }
                        })
                        .commit()
                }
            }



        }
        binding.btnContinueGameMainFrag.setOnClickListener {
//            GlobalScope.launch(Dispatchers.IO) {
//                Log.d("datas", db.dataDao().getGameNameById(iddata))
//                val data = db.dataDao().getPersonDataByGameId(iddata)[0].scoreList
//                var sum =0
//                val data2 = data.forEach {
//                    sum += it.toInt()
//                }
//                Log.d("datas", "" + sum)
//            }
            GlobalScope.launch(Dispatchers.IO) {
                iddata = db.dataDao().insertGame(StartGame(null, "시험")).toInt()
                db.dataDao().insertPerson(
                    PersonList(
                        null,
                        iddata,
                        "수정",
                        mutableListOf("3", "4", "7", "9", "10")
                    )
                )
                db.dataDao().insertPerson(
                    PersonList(
                        null,
                        iddata,
                        "원희",
                        mutableListOf("5", "4", "7", "9", "10")
                    )
                )
                db.dataDao().insertPerson(
                    PersonList(
                        null,
                        iddata,
                        "아재",
                        mutableListOf("3", "4", "7", "9", "10")
                    )
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                finish();
            }

        } else if (supportFragmentManager.backStackEntryCount == 1) {
            supportFragmentManager.popBackStack()
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            binding.mainactivitytitle.text = "보드게임 매니저"
        } else if (supportFragmentManager.backStackEntryCount == 2) {
            binding.mainactivitytitle.text = "보드게임 매니저"
            this.recreate()
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            for (i in 0..1) {
                supportFragmentManager.popBackStack()
            }
        }
    }


    fun getScreenSize(activity: Activity): Point? {
        val display = activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }
}