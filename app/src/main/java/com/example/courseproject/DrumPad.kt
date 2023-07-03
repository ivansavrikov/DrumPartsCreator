package com.example.courseproject

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import com.example.courseproject.core.ManeValues
import com.example.courseproject.databinding.FragmentDrumPadBinding
import com.example.courseproject.viewmodels.DataViewModel

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
                pressPad(view as Button)
            }
        }
        false
    }

    private fun playDrumCutItself(pad: Int, pool: Int){
        //mb
        ManeValues.soundPools[pool].play(pad, 1.0f, 1.0f, 0, 0, 1.0f)
    }
}