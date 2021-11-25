package com.wonddak.boardmaster.ui.fragment

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.wonddak.boardmaster.R
import com.wonddak.boardmaster.adapters.GameSettingRecyclerAdapter
import com.wonddak.boardmaster.databinding.FragmentGameSettingBinding
import com.wonddak.boardmaster.room.AppDatabase
import com.wonddak.boardmaster.room.PersonList
import com.wonddak.boardmaster.room.StartGame
import com.wonddak.boardmaster.ui.PersonAddDialog
import com.wonddak.boardmaster.ui.MainActivity
import com.wonddak.boardmaster.ui.ScoreBoardActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class GameSettingFragment : Fragment(), SensorEventListener {
    private var mainActivity: MainActivity? = null
    lateinit var binding: FragmentGameSettingBinding
    private lateinit var db: AppDatabase

    var myAdapter: GameSettingRecyclerAdapter? = null
    var myAdapter2: GameSettingRecyclerAdapter? = null

    val existpersonlist by lazy { requireArguments().getStringArrayList("exist") }
    var addpersonlist = mutableListOf<String>()
    val title by lazy { requireArguments().getString("title") }

    val mSensorManager by lazy { mainActivity!!.getSystemService(SENSOR_SERVICE) as SensorManager}
    val mAccelerometer by lazy { mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)}

    private var mShakeTime: Long = 0
    private val SHAKE_THRESHOLD_GRAVITY: Float = 2.7F
    private val SHAKE_SKIP_TIME: Int = 500


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameSettingBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        mainActivity!!.maintitle!!.text = "게임 설정"


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
        val prefs: SharedPreferences = requireContext().getSharedPreferences("boardgame", 0)
        val editor = prefs.edit()

        existpersonlist as MutableList<String>


        if (existpersonlist!!.size == 0) {
            changeVisibility(binding.existHeader)
            changeVisibility(binding.existPersonRecycler)
            changeVisibility(binding.btnShow)
        }

        myAdapter =
            GameSettingRecyclerAdapter(addpersonlist, requireContext(), this@GameSettingFragment, 0)
        myAdapter2 =
            GameSettingRecyclerAdapter(
                existpersonlist!!,
                requireContext(),
                this@GameSettingFragment,
                1
            )
        binding.existPersonRecycler.adapter = myAdapter2
        touchHelper.attachToRecyclerView(binding.addPersonRecycler)
        binding.addPersonRecycler.adapter = myAdapter

        binding.existHeader.setOnClickListener {
            changeVisibility2(binding.existPersonRecycler, binding.btnShow)
        }


        binding.btnAddNewPerson.setOnClickListener {
            PersonAddDialog(
                mainActivity!!
            ).addNewPersonDialog(addpersonlist, myAdapter!!)

        }

        binding.btnPerfect.setOnClickListener {
            when {
                binding.settingInputTitle.text.isEmpty() -> {
                    Snackbar.make(binding.root, "제목을 입력해주세요", Snackbar.LENGTH_SHORT)
                        .setAction("확인", null)
                        .show()
                }
                addpersonlist!!.size < 2 -> {
                    Snackbar.make(binding.root, "최소 참여 인원수는 2명입니다.", Snackbar.LENGTH_SHORT)
                        .setAction("확인", null)
                        .show()
                }
                else -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        val temp_id = db.dataDao()
                            .insertGame(
                                StartGame(
                                    null,
                                    binding.settingInputTitle.text.toString(),
                                    ""
                                )
                            )
                            .toInt()
                        editor.putInt("iddata", temp_id)
                        editor.commit()
                        launch(Dispatchers.Main) {
                            mainActivity!!.live_Id.value = temp_id
                        }
                        for (name in addpersonlist!!) {
                            db.dataDao()
                                .insertPerson(PersonList(null, temp_id, name))
                        }
                        launch(Dispatchers.Main) {
                            val intent = Intent(mainActivity!!, ScoreBoardActivity::class.java)
                            startActivity(intent)
                            mainActivity!!.overridePendingTransition(0, 0)
                            requireActivity().supportFragmentManager
                                .popBackStack()
                        }
                    }
                }
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

    private fun changeVisibility(view: View) {
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val axisX: Float = event.values[0]
            val axisY: Float = event.values[1]
            val axisZ: Float = event.values[2]

            val gravityX = axisX / SensorManager.GRAVITY_EARTH
            val gravityY = axisY / SensorManager.GRAVITY_EARTH
            val gravityZ = axisZ / SensorManager.GRAVITY_EARTH

            val f: Float = gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ
            val squaredD: Double = sqrt(f.toDouble())
            val gForce = squaredD.toFloat()

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val currnetTime :Long = System.currentTimeMillis()
                if (mShakeTime + SHAKE_SKIP_TIME > currnetTime) {
                    return
                }
                mShakeTime = currnetTime
                mShakeTime++
                addpersonlist.shuffle()
                myAdapter!!.notifyDataSetChanged()
                Toast.makeText(mainActivity!!,"순서를 무작위로 변경했습니다.",Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(
            this,
            mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    private fun changeVisibility2(view: View, view2: ImageView) {
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