package com.wonddak.boardmaster

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wonddak.boardmaster.databinding.ItemScoreInputBinding
import com.wonddak.boardmaster.databinding.ItemScoreNameBinding
import com.wonddak.boardmaster.databinding.ItemScoreRankBinding


class GameScoreBoardRecyclerAdapter(
    val personList: List<String>,
    val type: Int,
    val context: Context,

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TYPE_HEADER = 0
    val TYPE_ITEM = 1
    val TYPE_RANK = 2
    val TYPE_SUM = 3

//    val db = AppDatabase.getInstance(context)
//    val prefs: SharedPreferences = context.getSharedPreferences("boardgame", 0)
//    val iddata = prefs.getInt("iddata", 0)


    inner class ScoreHeaderViewHolder(binding: ItemScoreNameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var header = binding.itemPersonName
    }

    inner class ScoreSumViewHolder(binding: ItemScoreNameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var header = binding.itemPersonName
        var itemall = binding.itemAll
    }


    inner class ScoreViewHolder(binding: ItemScoreInputBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val item = binding.itemEditScore
    }

    inner class RankViewHolder(binding: ItemScoreRankBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var rank = binding.itemRank
    }


    override fun getItemViewType(position: Int): Int {
        return when (type) {
            0 -> {
                TYPE_HEADER
            }
            2 -> {
                TYPE_RANK
            }
            3 -> {
                TYPE_SUM
            }
            else -> {
                TYPE_ITEM
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_HEADER -> {
                val binding =
                    ItemScoreNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                sizeChange(binding.itemAll)
                return ScoreHeaderViewHolder(binding)
            }
            TYPE_RANK -> {
                val binding =
                    ItemScoreRankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return RankViewHolder(binding)
            }
            TYPE_SUM -> {
                val binding =
                    ItemScoreNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                sizeChange(binding.itemAll)
                return ScoreSumViewHolder(binding)
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
            is ScoreHeaderViewHolder -> {
                holder.header.text = personList[position]
            }
            is RankViewHolder -> {
                holder.rank.text = "#" + (position + 1)
            }
            is ScoreViewHolder -> {


            }
            is ScoreSumViewHolder -> {


            }
        }


    }


    override fun getItemCount(): Int {
        return when (type) {
            0 -> {
                personList.size
            }
//            1 -> {
//                scoreList.size
//            }
//            2 -> {
//                round
//            }
            else -> {
                personList.size
            }
        }
    }


    private fun sizeChange(view: View) {
        val displaymetrics = DisplayMetrics()
        (view.context as Activity).windowManager.defaultDisplay.getMetrics(displaymetrics)
        var devicewidth = displaymetrics.widthPixels

        devicewidth = if (personList.size == 2) {
            (devicewidth - dpToPx(context, 62)) / 2
        } else {
            (devicewidth - dpToPx(context, 60 + 1 * personList.size)) / 3
        }
        view.layoutParams.width = devicewidth
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