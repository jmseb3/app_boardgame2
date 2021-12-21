package com.wonddak.boardmaster.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.opengl.Visibility
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.wonddak.boardmaster.databinding.ItemScoreInputBinding
import com.wonddak.boardmaster.databinding.ItemScoreNameBinding
import com.wonddak.boardmaster.databinding.ItemScoreRankBinding
import com.wonddak.boardmaster.ui.ScoreBoardActivity
import java.lang.NumberFormatException


class GameScoreHeaderRecyclerAdapter(
    val personList: List<String>,
    val context: Context,
    val maxValueIDX: List<Int>,
    val wait :Boolean

) : RecyclerView.Adapter<GameScoreHeaderRecyclerAdapter.ScoreHeaderViewHolder>() {

    inner class ScoreHeaderViewHolder(binding: ItemScoreNameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var header = binding.itemPersonName
        val crown = binding.crown
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreHeaderViewHolder {
        val binding =
            ItemScoreNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        sizeChange(binding.itemAll)
        return ScoreHeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScoreHeaderViewHolder, position: Int) {
        holder.header.text = personList[position]
        if (wait) {
            holder.crown.visibility = View.INVISIBLE
        } else {
            if (position in maxValueIDX) {
                holder.crown.visibility = View.VISIBLE
            } else {
                holder.crown.visibility = View.INVISIBLE
            }
        }


    }

    override fun getItemCount(): Int {
        return personList.size
    }

//    private fun sizeChange(view: View) {
//        val displaymetrics = DisplayMetrics()
//        (view.context as Activity).windowManager.defaultDisplay.getMetrics(displaymetrics)
//        var device_width = displaymetrics.widthPixels
//        device_width = if (personList.size == 2) {
//            (device_width - dpToPx(context, 62)) / 2
//        } else {
//            (device_width - dpToPx(context, 60 + 1 * personList.size)) / 3
//        }
//        view.layoutParams.width = device_width
//        view.requestLayout()
//    }

    private fun sizeChange(view: View) {

        val displaymetrics = DisplayMetrics()
        (view.context as Activity).windowManager.defaultDisplay.getMetrics(displaymetrics)
        var device_width = displaymetrics.widthPixels
        if (personList.size == 2) {
            device_width = (((device_width * 0.865) / 2).toInt())
        } else {
            device_width = (((device_width * 0.865) / 3).toInt())
        }
        view.layoutParams.width = device_width
        view.requestLayout()
    }



}