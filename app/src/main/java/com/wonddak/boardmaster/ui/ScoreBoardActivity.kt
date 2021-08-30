package com.wonddak.boardmaster.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wonddak.boardmaster.R
import com.wonddak.boardmaster.adapters.GameScoreSumRecyclerAdapter
import com.wonddak.boardmaster.adapters.GameScoreBoardRecyclerAdapter
import com.wonddak.boardmaster.adapters.GameScoreHeaderRecyclerAdapter
import com.wonddak.boardmaster.databinding.ActivityScoreBoardBinding
import com.wonddak.boardmaster.room.AppDatabase
import com.wonddak.boardmaster.ui.viewmodels.ScoreBoardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class ScoreBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScoreBoardBinding

    var HeaderAdapter: GameScoreHeaderRecyclerAdapter? = null
    var RoundAdapter: GameScoreBoardRecyclerAdapter? = null
    var ScoreAdapter: GameScoreBoardRecyclerAdapter? = null
    var SumAdapter: GameScoreSumRecyclerAdapter? = null
    private lateinit var db: AppDatabase
    private var isFabOpen = false

    var personList: List<String> = mutableListOf()

    val TYPE_HEADER = 0
    val TYPE_ITEM = 1
    val TYPE_RANK = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        db = AppDatabase.getInstance(this)
        val viewModel = ViewModelProvider(this).get(ScoreBoardViewModel::class.java)

        dividerManger()
        scrollManger()

        GlobalScope.launch(Dispatchers.IO) {
            personList = viewModel.getPerson()
            val title = viewModel.getGame()
            viewModel.startBoardmap()
            RoundAdapter = GameScoreBoardRecyclerAdapter(
                personList,
                viewModel.board_map,
                TYPE_RANK,
                this@ScoreBoardActivity,
                this@ScoreBoardActivity,
                viewModel
            )
            ScoreAdapter = GameScoreBoardRecyclerAdapter(
                personList,
                viewModel.board_map,
                TYPE_ITEM,
                this@ScoreBoardActivity, this@ScoreBoardActivity, viewModel
            )
            viewModel.updateSumScore()
            launch(Dispatchers.Main) {
                binding.maintitle.text = "%s 게임".format(title)
                binding.gameRecyclerRound.adapter = RoundAdapter
                val gridLayoutManager = GridLayoutManager(this@ScoreBoardActivity, personList.size)
                binding.gameRecyclerScore.layoutManager = gridLayoutManager
                binding.gameRecyclerScore.adapter = ScoreAdapter
            }
        }

        viewModel.maxValueIDX.observe(this, Observer {
            HeaderAdapter = GameScoreHeaderRecyclerAdapter(personList, this, it, viewModel.wait)
            binding.gameRecyclerHeader.adapter = HeaderAdapter
        })

        viewModel.sum_socre.observe(this, Observer {
            Log.d("datas", "" + it)
            SumAdapter = GameScoreSumRecyclerAdapter(it, this@ScoreBoardActivity)
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


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_board, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> {
                val popMenu = PopupMenu(this, binding.toolbarMain, Gravity.RIGHT)
                popMenu.menuInflater.inflate(R.menu.menu_popup, popMenu.menu)
                popMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.nav_graph -> {
                            Toast.makeText(this, "graph", Toast.LENGTH_SHORT).show()
                        }
                        R.id.nav_list -> {
                            Toast.makeText(this, "list", Toast.LENGTH_SHORT).show()
                        }
                        R.id.nav_main -> {
                            Toast.makeText(this, "main", Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }
                popMenu.show()
            }
            R.id.action_finish -> {
                GameDialog(this, this).addEndDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        this.finish()
        overridePendingTransition(0, 0)
    }


    private fun scrollManger() {
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

    private fun dividerManger() {
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
