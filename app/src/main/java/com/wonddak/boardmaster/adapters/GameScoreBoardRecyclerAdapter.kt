package com.wonddak.boardmaster.adapters

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.wonddak.boardmaster.databinding.ItemScoreInputBinding
import com.wonddak.boardmaster.databinding.ItemScoreRankBinding
import com.wonddak.boardmaster.ui.ScoreBoardActivity
import com.wonddak.boardmaster.ui.viewmodels.ScoreBoardViewModel
import java.lang.NumberFormatException
import kotlin.math.round


class GameScoreBoardRecyclerAdapter(
    val personList: List<String>,
    var boardMap: MutableMap<Int, IntArray>,
    val type: Int,
    val context: Context,
    val activity: ScoreBoardActivity,
    val viewModel: ScoreBoardViewModel

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TYPE_ITEM = 1
    val TYPE_RANK = 2
    var req_pos = -1


    inner class ScoreViewHolder(binding: ItemScoreInputBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val item = binding.itemEditScore
    }

    inner class RankViewHolder(binding: ItemScoreRankBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var rank = binding.itemRank
        val all_rank = binding.rankAll
    }


    override fun getItemViewType(position: Int): Int {
        return when (type) {
            2 -> {
                TYPE_RANK
            }
            else -> {
                TYPE_ITEM
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_RANK -> {
                val binding =
                    ItemScoreRankBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return RankViewHolder(binding)
            }
            else -> {
                val binding = ItemScoreInputBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                sizeChange(binding.itemAll)
                return ScoreViewHolder(binding)

            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RankViewHolder -> {
                holder.rank.text = "#" + (position + 1)
                holder.all_rank.setOnClickListener {
                    boardMap[position + 1] = IntArray(personList.size) { 0 }
                    activity.ScoreAdapter!!.notifyDataSetChanged()
                    viewModel.updateSumScore()
                    viewModel.updateBoardmap(boardMap)
                    Toast.makeText(
                        context,
                        "" + (position + 1) + "라운드의 점수를 초기화 했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            is ScoreViewHolder -> {
                val round = position / personList.size
                val idx = position % personList.size
                holder.item.setText(boardMap[round + 1]!![idx].toString())
                if (position == req_pos) {
                    holder.item.requestFocus()
                    holder.item.selectAll()
                }
                holder.item.setOnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        viewModel.wait = false
                        val round = position / personList.size
                        val idx = position % personList.size
                        req_pos = position + 1
                        var is_append = true
                        try {
                            boardMap[round + 1]!![idx] = holder.item.text.toString().toInt()

                        } catch (e: NumberFormatException) {
                            holder.item.setText("0")
                            req_pos = position
                            Toast.makeText(context, "숫자를 입력해주세요", Toast.LENGTH_SHORT).show()
                            is_append = false
                        }
                        notifyDataSetChanged()
                        if ((position == boardMap.size * personList.size - 1) and is_append) {
                            boardMap[boardMap.size + 1] = IntArray(personList.size) { 0 }
                            activity.RoundAdapter!!.notifyDataSetChanged()
                        }
                        viewModel.updateSumScore()
                        viewModel.updateBoardmap(boardMap)

                        true
                    }
                    true

                }
            }

        }


    }

    private fun Cal_sum(): IntArray {
        val temp = IntArray(personList.size) { 0 }
        for (idx in personList.indices) {
            for (row in boardMap.values) {
                temp[idx] += row[idx]
            }

        }
        return temp
    }


    override fun getItemCount(): Int {
        return when (type) {
            0 -> {
                personList.size
            }
            1 -> {
                personList.size * boardMap.size
            }
            else -> {
                boardMap.size
            }
        }
    }


    private fun sizeChange(view: View) {

        val displaymetrics = DisplayMetrics()
        (view.context as Activity).windowManager.defaultDisplay.getMetrics(displaymetrics)
        var device_width = displaymetrics.widthPixels
        device_width = if (personList.size == 2) {
            (device_width - dpToPx(context, 62)) / 2
        } else {
            (device_width - dpToPx(context, 60 + 1 * personList.size)) / 3
        }
        view.layoutParams.width = device_width
        view.requestLayout()
    }


    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }


}