package com.wonddak.boardmaster.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.wonddak.boardmaster.ui.fragment.GameListFragment
import com.google.android.gms.ads.MobileAds


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private var backKeyPressedTime: Long = 0
    lateinit var mAdView : AdView
    var context: Context? = null
    var live_Id: MutableLiveData<Int> = MutableLiveData()
    var maintitle: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        mAdView  = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        setContentView(binding.root)
        db = AppDatabase.getInstance(this)
        setSupportActionBar(binding.toolbarMain)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        context = this
        maintitle = binding.mainactivitytitle
        MobileAds.initialize(this)


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
                    binding.mainactivitytitle.text = "게임 설정"
                    supportFragmentManager
                        .beginTransaction()
                        .addToBackStack(null)
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
            val intent = Intent(this, ScoreBoardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.btnGameList.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .add(R.id.frag_area, GameListFragment())
                .commit()
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
            supportFragmentManager.popBackStackImmediate()
            maintitle!!.text = resources.getString(R.string.app_name)
        }
    }

}