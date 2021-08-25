package com.wonddak.boardmaster.ui

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.wonddak.boardmaster.R
import com.wonddak.boardmaster.databinding.ActivityMainBinding
import com.wonddak.boardmaster.room.AppDatabase
import com.wonddak.boardmaster.ui.fragment.GameSettingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.ArrayList
import android.util.DisplayMetrics


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private var backKeyPressedTime: Long = 0
    var live_Id: MutableLiveData<Int> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = AppDatabase.getInstance(this)
        setSupportActionBar(binding.toolbarMain)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val prefs: SharedPreferences = this.getSharedPreferences("boardgame", 0)
        var iddata = prefs.getInt("iddata", 0)
        live_Id.value = iddata
        live_Id.observe(this, Observer {
            if (it == 0) {
                binding.btnContinueGameMainFrag.visibility = View.GONE
                binding.playGameInfo.visibility = View.GONE
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    val name_temp = db.dataDao().getGameNameById(it)
                    launch(Dispatchers.Main) {
                        binding.playGameInfo.text = "현재 \"$name_temp\"을(를) 플레이 중입니다."
                        binding.btnContinueGameMainFrag.visibility = View.VISIBLE
                        binding.playGameInfo.visibility = View.VISIBLE
                    }
                }
            }
        })




        binding.btnStartNewGameMainFrag.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val temp = db.dataDao().getPersonName().toSet().toMutableList()
                launch(Dispatchers.Main) {
                    supportFragmentManager
                        .beginTransaction()
                        .add(R.id.frag_area, GameSettingFragment().apply {
                            arguments = Bundle().apply {
                                putStringArrayList("exist", temp as ArrayList<String>)
                            }
                        })
                        .commit()
                }
            }

        }

        binding.btnContinueGameMainFrag.setOnClickListener {

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
        }
    }

}