package com.example.courseproject

import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.ToggleButton
import java.util.*
import kotlin.concurrent.timerTask

class RealTimeRhythm : AppCompatActivity() {
    private lateinit var metronomePlayer: MediaPlayer
    private lateinit var kickPlayer: MediaPlayer
    private lateinit var hihatPlayer: MediaPlayer
    private lateinit var snarePlayer: MediaPlayer

    private lateinit var openhatPlayer: MediaPlayer
    private lateinit var scratchPlayer: MediaPlayer
    private lateinit var clapPlayer: MediaPlayer
    private lateinit var cowbell1Player: MediaPlayer
    private lateinit var cowbell2Player: MediaPlayer
    private lateinit var cowbell3Player: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_rhythm)

        metronomePlayer = MediaPlayer.create(this, R.raw.metronome)
        kickPlayer = MediaPlayer.create(this, R.raw.kick_1)
        hihatPlayer = MediaPlayer.create(this, R.raw.hihat_1)
        snarePlayer = MediaPlayer.create(this, R.raw.snare_1)

        openhatPlayer = MediaPlayer.create(this, R.raw.openhat)
        scratchPlayer = MediaPlayer.create(this, R.raw.scratch)
        clapPlayer = MediaPlayer.create(this, R.raw.clap)
        cowbell1Player = MediaPlayer.create(this, R.raw.cowbell_long)
        cowbell2Player = MediaPlayer.create(this, R.raw.cowbell_long)
        cowbell3Player = MediaPlayer.create(this, R.raw.cowbell_long)

        val btnMetronome: ToggleButton = findViewById(R.id.btnMetronome)
        btnMetronome.setOnCheckedChangeListener(myCheckedChangeListener)

        val btnKick: Button = findViewById(R.id.btnKick)
        val btnHihat: Button = findViewById(R.id.btnHihat)
        val btnSnare: Button = findViewById(R.id.btnSnare)

        val btnOpenhat :Button = findViewById(R.id.btnOpenHat)
        val btnScratch :Button = findViewById(R.id.btnScratch)
        val btnClap :Button = findViewById(R.id.btnClap)
        val btnCowbell1 :Button = findViewById(R.id.btnCowbell1)
        val btnCowbell2 :Button = findViewById(R.id.btnCowbell2)
        val btnCowbell3 :Button = findViewById(R.id.btnCowbell3)

        val btnEdit: Button = findViewById(R.id.btnEdit)
        btnEdit.setOnTouchListener(drumTouchListener)

        btnKick.setOnTouchListener(drumTouchListener)
        btnHihat.setOnTouchListener(drumTouchListener)
        btnSnare.setOnTouchListener(drumTouchListener)

        btnOpenhat.setOnTouchListener(drumTouchListener)
        btnScratch.setOnTouchListener(drumTouchListener)
        btnClap.setOnTouchListener(drumTouchListener)
        btnCowbell1.setOnTouchListener(drumTouchListener)
        btnCowbell2.setOnTouchListener(drumTouchListener)
        btnCowbell3.setOnTouchListener(drumTouchListener)
    }

    private val drumTouchListener = object : View.OnTouchListener {
        override fun onTouch(view: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    when (view?.id) {
                        R.id.btnKick -> {
                            if(kickPlayer.isPlaying){
                                kickPlayer.pause()
                                kickPlayer.stop()
                                kickPlayer.prepare()
                       }
                            kickPlayer.start()
                        }

                        R.id.btnHihat -> {
                            hihatPlayer.start()
                        }

                        R.id.btnSnare -> {
                            snarePlayer.start()
                        }

                        R.id.btnOpenHat -> {
                            openhatPlayer.start()
                        }

                        R.id.btnScratch -> {
                            scratchPlayer.start()
                        }

                        R.id.btnClap -> {
                            clapPlayer.start()
                        }

                        R.id.btnCowbell1 -> {
                            cowbell1Player.start()
                        }

                        R.id.btnCowbell2 -> {
                            cowbell2Player.start()
                        }

                        R.id.btnCowbell3 -> {
                            cowbell3Player.start()
                        }

                        R.id.btnEdit -> {
                            intent = Intent(this@RealTimeRhythm, RhythmicGridActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    return true
                }
                else -> return false
            }
        }
    }

    private val myCheckedChangeListener =
        OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                metronomeStart()
            } else{
                metronomeStop()
            }
        }

    private var timer: Timer? = null
    private var tickCount: Int = 0

    private fun metronomeStart() {
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            tickCount++
            metronomePlayer.start()
        }, 0, (60_000 / 120).toLong())
    }

    private fun metronomeStop() {
        timer?.cancel()
        timer = null
        tickCount = 0
    }
}