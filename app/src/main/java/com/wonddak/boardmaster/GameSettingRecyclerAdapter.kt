package com.wonddak.boardmaster

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.wonddak.boardmaster.databinding.ItemSettingPersonBinding
import com.wonddak.boardmaster.databinding.ItemSettingPersonNameBinding
import java.util.*

class GameSettingRecyclerAdapter(
    val itemlist: MutableList<String>,
    val context: Context,
    val fragment: GameSettingFragment,
    val type: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TYPE_ADD = 0
    val TYPE_EXIST = 1

    inner class AddViewHolder(binding: ItemSettingPersonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.itemPersonName
        val btn_delete = binding.itemPersonDelete

        init {
            btn_delete.setOnClickListener {
                itemlist.removeAt(layoutPosition)
                notifyDataSetChanged()
            }

        }
    }

    inner class ExistViewHolder(binding: ItemSettingPersonNameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.itemPersonName

        init {
            name.setOnClickListener {
                if(itemlist.size ==1){
                    changeVisibility(fragment.binding.existHeader)
                    changeVisibility(fragment.binding.existPersonRecycler)
                    changeVisibility(fragment.binding.btnShow)
                }
                fragment.addpersonlist.add(name.text.toString())
                itemlist.removeAt(layoutPosition)
                fragment.myAdapter!!.notifyDataSetChanged()
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (type) {
            0 -> {
                TYPE_ADD
            }
            else -> {
                TYPE_EXIST
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ADD -> {
                val binding =
                    ItemSettingPersonBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                AddViewHolder(binding)
            }
            else -> {
                val binding =
                    ItemSettingPersonNameBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ExistViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddViewHolder -> {
                holder.name.text = itemlist[position]
            }
            is ExistViewHolder -> {
                holder.name.text = itemlist[position]

            }
        }

    }

    override fun getItemCount(): Int {
        return itemlist.size
    }

    fun onItemMove(fromPosition: Int?, toPosition: Int?): Boolean {
        fromPosition?.let {
            toPosition?.let {
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(itemlist, i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(itemlist, i, i - 1)
                    }
                }
                notifyItemMoved(fromPosition, toPosition)
                return true
            }
        }
        return false
    }

    private fun changeVisibility(view: View) {
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }


}

