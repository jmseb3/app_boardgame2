package com.wonddak.boardmaster.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.wonddak.boardmaster.adapters.GameSettingRecyclerAdapter
import com.wonddak.boardmaster.databinding.DialogAddNewPersonBinding
import com.wonddak.boardmaster.databinding.DialogGameEndBinding
import com.wonddak.boardmaster.databinding.DialogGameResultBinding
import com.wonddak.boardmaster.room.AppDatabase
import com.wonddak.boardmaster.ui.viewmodels.ScoreBoardViewModel


class PersonAddDialog(
    val context: Context
) {
    val dialog = Dialog(context)
    val params = dialog.window!!.attributes
    val db = AppDatabase.getInstance(context)
    val prefs: SharedPreferences = context.getSharedPreferences("boardgame", 0)
    val editor = prefs.edit()

    fun addNewPersonDialog(itemlist: MutableList<String>, adapter: GameSettingRecyclerAdapter) {
        val binding = DialogAddNewPersonBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
        dialog.show()

        binding.ok.setOnClickListener {
            itemlist.add(binding.addNewGameTitleText.text.toString())
            Toast.makeText(
                context,
                "" + binding.addNewGameTitleText.text.toString() + "님을 추가했습니다.",
                Toast.LENGTH_SHORT
            ).show()
            if (binding.checkDismiss.isChecked) {
                binding.addNewGameTitleText.setText("")
            } else {
                dialog.dismiss()
            }
            adapter.notifyDataSetChanged()

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

    fun addEndDialog() {
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
            addResultDialog()
        }

        binding.cancel.setOnClickListener {
            dialog.dismiss()
        }

    }

    fun addResultDialog() {
        val binding = DialogGameResultBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
        dialog.show()

        binding.ok.setOnClickListener {
            dialog.dismiss()
            activity.overridePendingTransition(0,0)
            activity.startActivity(Intent(activity, MainActivity::class.java))
            activity.finish()
            activity.overridePendingTransition(0,0)

        }

    }
}