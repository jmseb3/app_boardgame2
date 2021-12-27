package com.wonddak.boardmaster.ui.fragment.subitem

import android.content.Context
import android.media.SoundPool
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.wonddak.boardmaster.R
import com.wonddak.boardmaster.databinding.FragmentDiceBinding
import com.wonddak.boardmaster.ui.ScoreBoardActivity
import java.util.*

class DiceFragment : Fragment() {
    private var mainActivity: ScoreBoardActivity? = null
    lateinit var binding: FragmentDiceBinding
    private val rng: Random = Random()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val soundPool = SoundPool.Builder().build()
        val soundId = soundPool.load(mainActivity, R.raw.shake_dice, 1)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val soundcheck = sharedPreferences.getBoolean("dice_roll", true)
        binding = FragmentDiceBinding.inflate(inflater, container, false)

        binding.clearView.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .detach(this)
                .commit()
        }

        binding.diceImg.setOnClickListener {
            val randomNumber: Int = rng.nextInt(6) + 1
            if (soundcheck) {

            } else {
                soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)
            }
            roll_dice(randomNumber, binding.diceImg)
        }

        return binding.root
    }

    fun roll_dice(randomNumber: Int, view: ImageView) {
        when (randomNumber) {
            1 -> {
                Glide.with(this)
                    .load(R.drawable.one)
                    .into(view)
            }
            2 -> {
                Glide.with(this)
                    .load(R.drawable.two)
                    .into(view)
            }
            3 -> {
                Glide.with(this)
                    .load(R.drawable.three)
                    .into(view)
            }
            4 -> {
                Glide.with(this)
                    .load(R.drawable.four)
                    .into(view)
            }
            5 -> {
                Glide.with(this)
                    .load(R.drawable.five)
                    .into(view)
            }
            6 -> {
                Glide.with(this)
                    .load(R.drawable.six)
                    .into(view)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as ScoreBoardActivity

    }

    override fun onDetach() {
        super.onDetach()
        mainActivity = null
    }
}