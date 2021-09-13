package com.wonddak.boardmaster.ui.fragment.subitem

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wonddak.boardmaster.ui.ScoreBoardActivity
import android.os.CountDownTimer
import java.util.concurrent.TimeUnit
import android.widget.NumberPicker
import com.wonddak.boardmaster.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {
    private var mainActivity: ScoreBoardActivity? = null
    lateinit var binding: FragmentTimerBinding
    var time: Long = 0
    var cTimer: CountDownTimer? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTimerBinding.inflate(inflater, container, false)
        setPicker(binding.hourPick, 99, 0)
        setPicker(binding.minPick, 59, 0)
        setPicker(binding.secPick, 59, 0)

        fun startTimer(times: Long) {
            cTimer = object : CountDownTimer(times, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    var seconds = (millisUntilFinished / 1000).toInt()
                    val hours = seconds / (60 * 60)
                    val tempMint = seconds - hours * 60 * 60
                    val minutes = tempMint / 60
                    seconds = tempMint - minutes * 60
                    time -= 1000
                    binding.time.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                }

                override fun onFinish() {
                    binding.time.text = "00:00:00"
                    binding.settings.visibility = View.INVISIBLE
                    binding.pickers.visibility = View.VISIBLE
                }
            }
            cTimer!!.start()
        }

        fun cancelTimer() {
            if (cTimer != null) cTimer!!.cancel()
        }


        binding.start.setOnClickListener {
            val hour = binding.hourPick.value
            val min = binding.minPick.value
            val sec = binding.secPick.value.toLong()
            time = TimeUnit.SECONDS.toMillis((hour * 3600 + min * 60 + sec)) + 1000
            binding.settings.visibility = View.VISIBLE
            binding.pickers.visibility = View.INVISIBLE
            startTimer(time)
        }
        binding.pauseAndRestart.setOnClickListener {
            if (binding.pauseAndRestart.text == "일시 정지") {
                cancelTimer()
                binding.pauseAndRestart.text = "다시 시작"
            } else {
                startTimer(time)
                binding.pauseAndRestart.text = "일시 정지"
            }
        }
        binding.reset.setOnClickListener {
            cancelTimer()
            time = 0
            binding.time.text = "00:00:00"
            binding.settings.visibility = View.INVISIBLE
            binding.pickers.visibility = View.VISIBLE
        }
        binding.clearView.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .detach(this)
                .commit()

        }

        return binding.root
    }


    private fun setPicker(view: NumberPicker, maxvalue: Int, minvalue: Int) {
        view.value = 0
        view.maxValue = maxvalue
        view.minValue = minvalue
        view.setFormatter { String.format("%02d", it) }
        view.showDividers = 0
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as ScoreBoardActivity

    }

    override fun onDetach() {
        super.onDetach()
        if (cTimer != null) cTimer!!.cancel()
        time =0
        mainActivity = null
    }
}