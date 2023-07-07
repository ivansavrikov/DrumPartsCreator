package com.ionedeveloper.rhythmcomposer

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ionedeveloper.rhythmcomposer.core.ManeValues
import com.ionedeveloper.rhythmcomposer.databinding.FragmentDrumPadBinding
import com.ionedeveloper.rhythmcomposer.viewmodels.DataViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DrumPad : Fragment() {
    private val dataModel : DataViewModel by activityViewModels()

    private lateinit var binding : FragmentDrumPadBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrumPadBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPad1.setOnTouchListener(drumTouchListener)
        binding.btnPad2.setOnTouchListener(drumTouchListener)
        binding.btnPad3.setOnTouchListener(drumTouchListener)
        binding.btnPad4.setOnTouchListener(drumTouchListener)
        binding.btnPad5.setOnTouchListener(drumTouchListener)
        binding.btnPad6.setOnTouchListener(drumTouchListener)
        binding.btnPad7.setOnTouchListener(drumTouchListener)
        binding.btnPad8.setOnTouchListener(drumTouchListener)
        binding.btnPad9.setOnTouchListener(drumTouchListener)
        binding.btnPad10.setOnTouchListener(drumTouchListener)
        binding.btnPad11.setOnTouchListener(drumTouchListener)
        binding.btnPad12.setOnTouchListener(drumTouchListener)

        binding.btnPlay.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                playback()
            } else{
                currentPlayback?.cancel()
            }
        }

        val navController = findNavController()
        binding.ivSettings.setOnClickListener {
            navController.navigate(R.id.settings)
        }

    }

    fun Fragment.vibratePhone() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(80)
        }
    }

    private var currentPlayback : Job? = null

    private fun playback(){
        currentPlayback?.cancel()
        if (binding.btnPlay.isChecked){
            currentPlayback = playAllPatterns()
            currentPlayback?.start()
        } else if(!binding.btnPlay.isChecked){
            currentPlayback?.cancel()
        }
    }

    private fun pressPad(pad: Button){
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            pad,
            PropertyValuesHolder.ofFloat("scaleX", 0.90f),
            PropertyValuesHolder.ofFloat("scaleY", 0.90f)
        )
        scaleDown.duration = 50
        scaleDown.repeatCount = 1
        scaleDown.repeatMode = ObjectAnimator.REVERSE

        scaleDown.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                pad.scaleX = 1f
                pad.scaleY = 1f
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        scaleDown.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    private val drumTouchListener = View.OnTouchListener { view, event ->
        when(event?.actionMasked){
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                when (view?.id) {
                    R.id.btnPad1 -> {
                        playDrumCutItself(ManeValues.pads[0], 0)
                    }

                    R.id.btnPad2 -> {
                        playDrumCutItself(ManeValues.pads[1], 1)
                    }

                    R.id.btnPad3 -> {
                        playDrumCutItself(ManeValues.pads[2], 2)
                    }

                    R.id.btnPad4 -> {
                        playDrumCutItself(ManeValues.pads[3], 3)
                    }

                    R.id.btnPad5 -> {
                        playDrumCutItself(ManeValues.pads[4], 4)
                    }

                    R.id.btnPad6 -> {
                        playDrumCutItself(ManeValues.pads[5], 5)
                    }

                    R.id.btnPad7 -> {
                        playDrumCutItself(ManeValues.pads[6], 6)
                    }

                    R.id.btnPad8 -> {
                        playDrumCutItself(ManeValues.pads[7], 7)
                    }

                    R.id.btnPad9 -> {
                        playDrumCutItself(ManeValues.pads[8], 8)
                    }

                    R.id.btnPad10 -> {
                        playDrumCutItself(ManeValues.pads[9], 9)
                    }

                    R.id.btnPad11 -> {
                        playDrumCutItself(ManeValues.pads[10], 10)
                    }

                    R.id.btnPad12 -> {
                        playDrumCutItself(ManeValues.pads[11], 11)
                    }
                }
                if(dataModel.vibrateSetting.value == true) vibratePhone()
                pressPad(view as Button)
            }
        }
        false
    }

    private fun playDrumCutItself(pad: Int, pool: Int){
        ManeValues.soundPools[pool].play(pad, 1.0f, 1.0f, 0, 0, 1.0f)
    }


    private val mutex = Mutex()
    private var startTime = System.currentTimeMillis() //начало воспроизведение ритмического шага
    private var endTime = System.currentTimeMillis() //конец воспроизведение ритмического шага
    private var totalTime = System.currentTimeMillis() //общее время воспроизведение ритмического шага
    private fun playAllPatterns() : Job {
        return CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY) {
            mutex.withLock {
                ensureActive()
                while (true){
                    repeat(ManeValues.currentPattern.size){ step ->
                        startTime = System.currentTimeMillis()
                        ManeValues.pads.forEachIndexed{ pattern, pad ->
                            if(ManeValues.patterns[pattern][step])
                                ManeValues.soundPools[pattern].play(pad, 1.0f, 1.0f, 0, 0, 1.0f)
                        }
                        endTime = System.currentTimeMillis()
                        totalTime = endTime - startTime
                        delay(ManeValues.stepDuration - totalTime)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        currentPlayback?.cancel()
    }
    override fun onStop() {
        super.onStop()
        currentPlayback?.cancel()
        binding.btnPlay.isChecked = false
    }
    override fun onDestroy() {
        super.onDestroy()
        currentPlayback?.cancel()
    }
}