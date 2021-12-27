package com.wonddak.boardmaster.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.wonddak.boardmaster.adapters.GameResultRecyclerAdapter
import com.wonddak.boardmaster.adapters.GameSettingRecyclerAdapter
import com.wonddak.boardmaster.databinding.DialogAddNewPersonBinding
import com.wonddak.boardmaster.databinding.DialogGameEndBinding
import com.wonddak.boardmaster.databinding.DialogGameResultBinding
import com.wonddak.boardmaster.room.AppDatabase
import com.wonddak.boardmaster.ui.viewmodels.ScoreBoardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PersonAddDialog(
    val context: Context
) {
    val dialog = Dialog(context)
    val params = dialog.window!!.attributes
    val prefs: SharedPreferences = context.getSharedPreferences("boardgame", 0)

    fun addNewPersonDialog(itemlist: MutableList<String>, adapter: GameSettingRecyclerAdapter) {
        val binding = DialogAddNewPersonBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val ischeck = sharedPreferences.getBoolean("add_person", true)

        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
        dialog.show()

        binding.ok.setOnClickListener {
            val get_text = binding.addNewGameTitleText.text.toString()
            if (get_text.isEmpty() or get_text.isBlank()) {
                Toast.makeText(
                    context,
                    "이름을 입력해 주세요",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                itemlist.add(get_text)
                Toast.makeText(
                    context,
                    "" + get_text + "님을 추가했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                if (ischeck) {
                    binding.addNewGameTitleText.setText("")
                } else {
                    dialog.dismiss()
                }
                adapter.notifyDataSetChanged()

            }
        }

        binding.cancel.setOnClickListener {
            dialog.dismiss()
        }


    }

}

class GameDialog(
    val context: Context,
    val activity: ScoreBoardActivity
) {
    val dialog = Dialog(context)
    val params = dialog.window!!.attributes
    val viewModel = ViewModelProvider(activity).get(ScoreBoardViewModel::class.java)

    val db = AppDatabase.getInstance(context)
    val prefs: SharedPreferences = context.getSharedPreferences("boardgame", 0)
    val editor = prefs.edit()

    fun addEndDialog(mInterstitialAd: InterstitialAd?, activitys: ScoreBoardActivity) {
        val binding = DialogGameEndBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
        dialog.show()

        binding.ok.setOnClickListener {
            dialog.dismiss()
            editor.putInt("iddata", 0)
            editor.commit()
            addResultDialog(mInterstitialAd, activitys)
        }

        binding.cancel.setOnClickListener {
            dialog.dismiss()
        }

    }

    data class GameResult(
        val score: Int,
        val name: String
    )

    fun addResultDialog(mInterstitialAd: InterstitialAd?, activitys: ScoreBoardActivity) {
        val binding = DialogGameResultBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
        dialog.show()

        GlobalScope.launch(Dispatchers.IO) {
            var personlist = viewModel.getPerson()
            var sumlist = viewModel.sum_socre.value
            var datalist = mutableListOf<GameResult>()
            for (x in personlist.indices) {
                datalist.add(GameResult(sumlist!![x], personlist[x]))
            }
            datalist.sortByDescending { it.score }


            GlobalScope.launch(Dispatchers.Main) {
                binding.resultRecycler.adapter = GameResultRecyclerAdapter(datalist, context)
                Log.d("datas", "" + datalist)
            }
        }



        binding.ok.setOnClickListener {
            dialog.dismiss()
            activity.overridePendingTransition(0, 0)
            activity.startActivity(Intent(activity, MainActivity::class.java))
            activity.finish()
            activity.overridePendingTransition(0, 0)
            mInterstitialAd?.show(activitys)
        }

    }
}