package com.wonddak.boardmaster.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wonddak.boardmaster.R
import com.wonddak.boardmaster.adapters.GameListRecyclerAdapter
import com.wonddak.boardmaster.databinding.FragmentGameListBinding
import com.wonddak.boardmaster.databinding.FragmentGameSettingBinding
import com.wonddak.boardmaster.room.AppDatabase
import com.wonddak.boardmaster.ui.MainActivity

class GameListFragment : Fragment() {
    private var mainActivity: MainActivity? = null
    lateinit var binding: FragmentGameListBinding
    private lateinit var db: AppDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameListBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        mainActivity!!.maintitle!!.text = "게임 목록"

        val divider_Vertical = DividerItemDecoration(mainActivity!!, LinearLayoutManager.VERTICAL)
        divider_Vertical.setDrawable(this.resources.getDrawable(R.drawable.divider_vertical, null))
        binding.listRecycler.addItemDecoration(divider_Vertical)


        db.dataDao().getLiveGameNameAndPerson().observe(mainActivity!!, Observer {
            try {
                binding.listRecycler.adapter = GameListRecyclerAdapter(it, mainActivity!!)
            } catch (e: NullPointerException) {

            }
        })




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

