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
import com.example.courseproject.core.ManeValues

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DrumPad.newInstance] factory method to
 * create an instance of this fragment.
 */
class DrumPad : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drum_pad, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DrumPad.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DrumPad().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnPad1: Button = view.findViewById(R.id.btnPad1)
        val btnPad2: Button = view.findViewById(R.id.btnPad2)
        val btnPad3: Button = view.findViewById(R.id.btnPad3)
        val btnPad4 :Button = view.findViewById(R.id.btnPad4)
        val btnPad5 :Button = view.findViewById(R.id.btnPad5)
        val btnPad6 :Button = view.findViewById(R.id.btnPad6)
        val btnPad7 :Button = view.findViewById(R.id.btnPad7)
        val btnPad8 :Button = view.findViewById(R.id.btnPad8)
        val btnPad9 :Button = view.findViewById(R.id.btnPad9)
        val btnPad10 :Button = view.findViewById(R.id.btnPad10)
        val btnPad11 :Button = view.findViewById(R.id.btnPad11)
        val btnPad12 :Button = view.findViewById(R.id.btnPad12)

        btnPad1.setOnTouchListener(drumTouchListener)
        btnPad2.setOnTouchListener(drumTouchListener)
        btnPad3.setOnTouchListener(drumTouchListener)
        btnPad4.setOnTouchListener(drumTouchListener)
        btnPad5.setOnTouchListener(drumTouchListener)
        btnPad6.setOnTouchListener(drumTouchListener)
        btnPad7.setOnTouchListener(drumTouchListener)
        btnPad8.setOnTouchListener(drumTouchListener)
        btnPad9.setOnTouchListener(drumTouchListener)
        btnPad10.setOnTouchListener(drumTouchListener)
        btnPad11.setOnTouchListener(drumTouchListener)
        btnPad12.setOnTouchListener(drumTouchListener)
    }

    private fun pressPad(pad: Button){
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            pad,
            PropertyValuesHolder.ofFloat("scaleX", 0.90f),
            PropertyValuesHolder.ofFloat("scaleY", 0.90f)
        )
        scaleDown.duration = 85
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

    private val drumTouchListener = object : View.OnTouchListener {
        override fun onTouch(view: View?, event: MotionEvent?): Boolean {
            when(event?.actionMasked){
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    when (view?.id) {
                        R.id.btnPad1 -> {
                            playDrumCutItself(ManeValues.pads[0], 0) //проигрываем соответсвующий звук
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
            return false
        }
    }

    private fun playDrumCutItself(pad: Int, pool: Int){
//        ManeValues.soundPools[pool].stop(pad)
        ManeValues.soundPools[pool].play(pad, 1.0f, 1.0f, 0, 0, 1.0f)
    }
}