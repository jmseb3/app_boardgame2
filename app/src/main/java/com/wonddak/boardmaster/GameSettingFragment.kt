package com.wonddak.boardmaster

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import com.wonddak.boardmaster.databinding.FragmentGameSettingBinding
import com.wonddak.boardmaster.room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GameSettingFragment : Fragment() {
    private var mainActivity: MainActivity? = null
    private lateinit var binding: FragmentGameSettingBinding
    private lateinit var db: AppDatabase

    private var myAdapter: GameSettingRecyclerAdapter? = null
    private var myAdapter2: GameSettingRecyclerAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameSettingBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())

        val prefs: SharedPreferences = requireContext().getSharedPreferences("game", 0)
        val editor = prefs.edit()
        var addpersonlist = mutableListOf<String>("수정", "사랑해", "정말", "수정", "아사", "랑해", "정말")
        var existpersonlist = mutableListOf<String>()
        GlobalScope.launch(Dispatchers.IO) {
            existpersonlist = db.dataDao().getPersonName().toSet().toMutableList()
            launch(Dispatchers.Main) {
                myAdapter2 = GameSettingRecyclerAdapter(existpersonlist, requireContext(), 1)
                binding.existPersonRecycler.adapter = myAdapter2
            }
        }
        myAdapter = GameSettingRecyclerAdapter(addpersonlist, requireContext(), 0)
        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(UP + DOWN, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                myAdapter!!.onItemMove(viewHolder?.layoutPosition, target?.layoutPosition);
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }
        })
        touchHelper.attachToRecyclerView(binding.addPersonRecycler)
        binding.addPersonRecycler.adapter = myAdapter



        binding.btnAddNewPerson.setOnClickListener {
            Log.d("datas", "" + addpersonlist)
            changeVisibility(binding.existHeader)
            changeVisibility(binding.existPersonRecycler)
        }

//        GlobalScope.launch(Dispatchers.IO) {
//            iddata = db.dataDao().insertGame(StartGame(null, "시험")).toInt()
//            db.dataDao().insertPerson(
//                PersonList(
//                    null,
//                    iddata,
//                    "수정",
//                    mutableListOf("3", "4", "7", "9", "10")
//                )
//            )
//
//            editor.putInt("iddata", iddata)
//            editor.commit()
//        }


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

    private fun changeVisibility(view: View) {
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }


}