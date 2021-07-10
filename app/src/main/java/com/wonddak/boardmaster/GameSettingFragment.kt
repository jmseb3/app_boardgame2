package com.wonddak.boardmaster

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wonddak.boardmaster.databinding.FragmentGameSettingBinding
import com.wonddak.boardmaster.room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GameSettingFragment : Fragment() {
    private var mainActivity: MainActivity? = null
    lateinit var binding: FragmentGameSettingBinding
    private lateinit var db: AppDatabase

    var myAdapter: GameSettingRecyclerAdapter? = null
    var myAdapter2: GameSettingRecyclerAdapter? = null

    val existpersonlist by lazy { requireArguments().getStringArrayList("exist") }
    var addpersonlist = mutableListOf<String>()
    val title by lazy { requireArguments().getString("title") }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameSettingBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())

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
        val prefs: SharedPreferences = requireContext().getSharedPreferences("game", 0)
        val editor = prefs.edit()

        existpersonlist as MutableList<String>

        myAdapter =
            GameSettingRecyclerAdapter(addpersonlist, requireContext(), this@GameSettingFragment, 0)
        myAdapter2 =
            GameSettingRecyclerAdapter( existpersonlist!!,requireContext(),this@GameSettingFragment, 1)
        binding.existPersonRecycler.adapter = myAdapter2
        touchHelper.attachToRecyclerView(binding.addPersonRecycler)
        binding.addPersonRecycler.adapter = myAdapter

        binding.existHeader.setOnClickListener {
            changeVisibility2(binding.existPersonRecycler,binding.btnShow)
        }



        binding.btnAddNewPerson.setOnClickListener {
            AddDialog(
                requireContext(),
                mainActivity!!
            ).addNewPersonDialog(addpersonlist,myAdapter!!)

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

    private fun changeVisibility2(view: View,view2:ImageView) {
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
            Glide.with(requireContext())
                .load(R.drawable.ic_baseline_arrow_drop_up_24)
                .into(view2)
        } else {
            view.visibility = View.GONE
            Glide.with(requireContext())
                .load(R.drawable.ic_baseline_arrow_drop_down_24)
                .into(view2)
        }
    }



}