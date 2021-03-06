package com.wonddak.boardmaster.adapters

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wonddak.boardmaster.databinding.ItemScoreNameBinding

class GameScoreSumRecyclerAdapter(
    val sumlist: IntArray,
    val context: Context

) : RecyclerView.Adapter<GameScoreSumRecyclerAdapter.SumViewHolder>() {

    inner class SumViewHolder(binding: ItemScoreNameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var header = binding.itemPersonName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SumViewHolder {
        val binding =
            ItemScoreNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        sizeChange(binding.itemAll)
        return SumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SumViewHolder, position: Int) {
        holder.header.text = sumlist[position].toString()
    }

    override fun getItemCount(): Int {
        return sumlist.size
    }

    private fun sizeChange(view: View) {

        val displaymetrics = DisplayMetrics()
        (view.context as Activity).windowManager.defaultDisplay.getMetrics(displaymetrics)
        var device_width = displaymetrics.widthPixels
        if (sumlist.size == 2) {
            device_width = (((device_width * 0.865) / 2).toInt())
        } else {
            device_width = (((device_width * 0.865) / 3).toInt())
        }
        view.layoutParams.width = device_width
        view.requestLayout()
    }

}