package com.wonddak.boardmaster

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wonddak.boardmaster.databinding.FragmentGameScoreBoardBinding
import com.wonddak.boardmaster.databinding.FragmentGameSettingBinding
import com.wonddak.boardmaster.room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GameScoreBoardFragment : Fragment() {
    private var mainActivity: MainActivity? = null
    lateinit var binding: FragmentGameScoreBoardBinding
    private lateinit var db: AppDatabase

    var personList :List<String> = mutableListOf()

    val TYPE_HEADER = 0
    val TYPE_ITEM = 1
    val TYPE_RANK = 2
    val TYPE_SUM = 3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameScoreBoardBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())

        val prefs: SharedPreferences = mainActivity!!.getSharedPreferences("boardgame", 0)
        var iddata = prefs.getInt("iddata", 0)


        GlobalScope.launch {
            personList = db.dataDao().getPersonNameByGameId(iddata)
            launch(Dispatchers.Main) {
                binding.gameRecyclerHeader.adapter = GameScoreBoardRecyclerAdapter(personList,TYPE_HEADER,mainActivity!!)
            }
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity

    }

    override fun onDetach() {
        super.onDetach()
        mainActivity = null
    }

}