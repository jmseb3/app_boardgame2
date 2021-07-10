package com.wonddak.boardmaster

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.wonddak.boardmaster.databinding.DialogAddNewPersonBinding
import com.wonddak.boardmaster.room.AppDatabase


class AddDialog(
        val context: Context,
        val activity: MainActivity
) {
    val dialog = Dialog(context)
    val params = dialog.window!!.attributes
    val db = AppDatabase.getInstance(context)
    val prefs: SharedPreferences = context.getSharedPreferences("game", 0)
    val editor = prefs.edit()

    fun addNewPersonDialog(itemlist:MutableList<String>,adapter: GameSettingRecyclerAdapter) {
        val binding = DialogAddNewPersonBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
        dialog.show()

        binding.ok.setOnClickListener {
            itemlist.add(binding.addNewGameTitleText.text.toString())

            Toast.makeText(context, "" + binding.addNewGameTitleText.text.toString() + "님을 추가했습니다.", Toast.LENGTH_SHORT).show()
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