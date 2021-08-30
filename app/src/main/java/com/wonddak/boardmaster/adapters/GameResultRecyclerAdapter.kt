package com.wonddak.boardmaster.adapters

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wonddak.boardmaster.databinding.ItemResultBinding
import com.wonddak.boardmaster.databinding.ItemScoreNameBinding
import com.wonddak.boardmaster.ui.GameDialog
import com.wonddak.boardmaster.ui.viewmodels.ScoreBoardViewModel

class GameResultRecyclerAdapter(
    val datalist: List<GameDialog.GameResult>,
    val context: Context,

    ) : RecyclerView.Adapter<GameResultRecyclerAdapter.ResultViewHolder>() {


    inner class ResultViewHolder(binding: ItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var rank = binding.resultRank
        var name = binding.resultName
        var score = binding.resultScore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val rank = IntArray(datalist.size) { 0 }
        for (x in datalist.indices) {
            if (x == 0) {
                rank[0] = 1
            } else {
                if (datalist[x - 1].score == datalist[x].score) {
                    rank[x] = rank[x - 1]
                } else {
                    rank[x] = x + 1
                }
            }

        }
        holder.rank.text = rank[position].toString() + "등"
        holder.name.text = datalist[position].name
        holder.score.text = datalist[position].score.toString() + "점"

    }

    override fun getItemCount(): Int {
        return datalist.size
    }

}