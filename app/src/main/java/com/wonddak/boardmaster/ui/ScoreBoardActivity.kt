package com.wonddak.boardmaster.ui

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wonddak.boardmaster.R
import com.wonddak.boardmaster.adapters.GamScoreSumRecyclerAdapter
import com.wonddak.boardmaster.adapters.GameScoreBoardRecyclerAdapter
import com.wonddak.boardmaster.adapters.GameScoreHeaderRecyclerAdapter
import com.wonddak.boardmaster.databinding.ActivityScoreBoardBinding
import com.wonddak.boardmaster.room.AppDatabase
import com.wonddak.boardmaster.ui.viewmodels.ScoreBoardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.stream.IntStream

class ScoreBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScoreBoardBinding

    var HeaderAdapter: GameScoreHeaderRecyclerAdapter? = null
    var RoundAdapter: GameScoreBoardRecyclerAdapter? = null
    var ScoreAdapter: GameScoreBoardRecyclerAdapter? = null
    var SumAdapter: GamScoreSumRecyclerAdapter? = null
    private lateinit var db: AppDatabase

    var personList: List<String> = mutableListOf()

    val TYPE_HEADER = 0
    val TYPE_ITEM = 1
    val TYPE_RANK = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)
        val prefs: SharedPreferences = this.getSharedPreferences("boardgame", 0)
        var iddata = prefs.getInt("iddata", 0)

        val viewModel = ViewModelProvider(this).get(ScoreBoardViewModel::class.java)


        DividerManger()

        binding.test.setOnClickListener {
            Log.d("datas",""+viewModel.board_map)
        }

        GlobalScope.launch(Dispatchers.IO) {
            personList = viewModel.getPerson()
            viewModel.startBoardmap()
            RoundAdapter = GameScoreBoardRecyclerAdapter(
                personList,
                viewModel.board_map,
                TYPE_RANK,
                this@ScoreBoardActivity,
                viewModel.sum_socre, this@ScoreBoardActivity
            )
            ScoreAdapter = GameScoreBoardRecyclerAdapter(
                personList,
                viewModel.board_map,
                TYPE_ITEM,
                this@ScoreBoardActivity,
                viewModel.sum_socre, this@ScoreBoardActivity
            )
            launch(Dispatchers.Main) {
                viewModel.updateSumScore()
                binding.gameRecyclerRound.adapter = RoundAdapter
                val gridLayoutManager = GridLayoutManager(this@ScoreBoardActivity, personList.size)
                binding.gameRecyclerScore.layoutManager = gridLayoutManager
                binding.gameRecyclerScore.adapter = ScoreAdapter
            }
        }

        viewModel.maxValueIDX.observe(this, Observer {
            HeaderAdapter = GameScoreHeaderRecyclerAdapter(personList, this, it)
            binding.gameRecyclerHeader.adapter = HeaderAdapter
        })

        viewModel.sum_socre.observe(this, Observer {
            Log.d("datas", "" + it)
            SumAdapter = GamScoreSumRecyclerAdapter(it, this@ScoreBoardActivity)
            binding.gameRecyclerSum.adapter = SumAdapter
            val max_val = it.maxOrNull() ?: 0
            val temp = mutableListOf<Int>()
            for (x in it.indices) {
                if (it[x] == max_val) {
                    temp.add(x)
                }
            }
            viewModel.updateMaxValueIdx(temp)

        })

        ScrollManger()

    }


    private fun ScrollManger() {
        val headerScroll = binding.headerScroll
        val roundScroll = binding.roundScroll
        val scoreHorizontalScroll = binding.gameScore
        val scoreVerticalScroll = binding.gameScoreVerticalScroll
        val scrollSum = binding.sumScroll
        headerScroll.setOnScrollChangeListener { _, scrollX, scrollY, _, _ ->
            scoreHorizontalScroll.scrollTo(scrollX, scrollY)
            scrollSum.scrollTo(scrollX, scrollY)
        }
        roundScroll.setOnScrollChangeListener { _, scrollX, scrollY, _, _ ->
            scoreVerticalScroll.scrollTo(scrollX, scrollY)
        }

        scoreVerticalScroll.setOnScrollChangeListener { _, scrollX, scrollY, _, _ ->
            roundScroll.scrollTo(scrollX, scrollY)
        }

        scoreHorizontalScroll.setOnScrollChangeListener { _, scrollX, scrollY, _, _ ->
            headerScroll.scrollTo(scrollX, scrollY)
            scrollSum.scrollTo(scrollX, scrollY)
        }
        scrollSum.setOnScrollChangeListener { _, scrollX, scrollY, _, _ ->
            scoreHorizontalScroll.scrollTo(scrollX, scrollY)
            headerScroll.scrollTo(scrollX, scrollY)
        }
    }

    private fun DividerManger() {
        val divider_Vertical = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        val divider_Horizontal = DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL)
        val grid_divider_Vertical = DividerItemDecoration(this, GridLayoutManager.VERTICAL)
        val grid_divider_Horizontal = DividerItemDecoration(this, GridLayoutManager.HORIZONTAL)
        divider_Vertical.setDrawable(this.resources.getDrawable(R.drawable.divider_vertical, null))
        divider_Horizontal.setDrawable(
            this.resources.getDrawable(
                R.drawable.divider_vertical,
                null
            )
        )
        grid_divider_Vertical.setDrawable(
            this.resources.getDrawable(
                R.drawable.divider_vertical,
                null
            )
        )
        grid_divider_Horizontal.setDrawable(
            this.resources.getDrawable(
                R.drawable.divider_vertical,
                null
            )
        )
        binding.gameRecyclerHeader.addItemDecoration(divider_Horizontal)
        binding.gameRecyclerScore.addItemDecoration(grid_divider_Vertical)
        binding.gameRecyclerScore.addItemDecoration(grid_divider_Horizontal)
        binding.gameRecyclerRound.addItemDecoration(divider_Vertical)
        binding.gameRecyclerSum.addItemDecoration(divider_Horizontal)
    }


}
