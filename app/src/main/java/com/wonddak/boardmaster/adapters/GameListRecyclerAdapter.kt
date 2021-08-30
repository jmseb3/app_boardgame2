package com.wonddak.boardmaster.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wonddak.boardmaster.R
import com.wonddak.boardmaster.databinding.ItemGameListBinding
import com.wonddak.boardmaster.databinding.ItemResultBinding
import com.wonddak.boardmaster.databinding.ItemScoreNameBinding
import com.wonddak.boardmaster.room.StartGameWithPersonList
import com.wonddak.boardmaster.ui.GameDialog
import com.wonddak.boardmaster.ui.MainActivity
import com.wonddak.boardmaster.ui.ScoreBoardActivity
import com.wonddak.boardmaster.ui.fragment.GameListFragment
import com.wonddak.boardmaster.ui.viewmodels.ScoreBoardViewModel

class GameListRecyclerAdapter(
    val datalist: List<StartGameWithPersonList>,
    val activity: MainActivity

) : RecyclerView.Adapter<GameListRecyclerAdapter.GameListViewHolder>() {

    val prefs: SharedPreferences = activity.getSharedPreferences("boardgame", 0)
    val editor = prefs.edit()
    var iddata = prefs.getInt("iddata", 0)

    inner class GameListViewHolder(binding: ItemGameListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.gameListName
        val personcnt = binding.gameListPerson
        val arrow = binding.rightArrow
        val allitem = binding.itemAll
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameListViewHolder {
        val binding =
            ItemGameListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameListViewHolder, position: Int) {
        val GameInfo = datalist[position].StartGame
        val personList = datalist[position].PersonList
        holder.name.text = "게임이름: %s".format(GameInfo.gameName)
        holder.personcnt.text = "%s 외 %d명".format(personList[0].personName, (personList.size - 1))

        holder.allitem.setOnClickListener {
            editor.putInt("iddata", GameInfo.gameId!!)
            editor.commit()
            activity.live_Id.value = GameInfo.gameId!!
            val intent = Intent(activity, ScoreBoardActivity::class.java)
            activity.startActivity(intent)
            activity.overridePendingTransition(0, 0)
            activity.supportFragmentManager
                .popBackStack()
        }

    }

    override fun getItemCount(): Int {
        return datalist.size
    }

}