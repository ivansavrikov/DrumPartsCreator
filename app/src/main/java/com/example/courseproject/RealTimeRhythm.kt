package com.example.courseproject

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.example.courseproject.core.ManeValues
import java.util.*
import kotlin.concurrent.timerTask

class RealTimeRhythm : AppCompatActivity() {
    private lateinit var editTextBpm: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_rhythm)

        editTextBpm = findViewById(R.id.editTextBpm)

        ManeValues.metronomePlayer = MediaPlayer.create(this, R.raw.metronome)
        ManeValues.metronomeBeepDPlayer = MediaPlayer.create(this, R.raw.metronome_beep_d)
        ManeValues.metronomeBeepCPlayer = MediaPlayer.create(this, R.raw.metronome_beep_c)

        ManeValues.kickPlayer = MediaPlayer.create(this, R.raw.kick_1)
        ManeValues.hihatPlayer = MediaPlayer.create(this, R.raw.hihat_1)
        ManeValues.snarePlayer = MediaPlayer.create(this, R.raw.snare_1)
        ManeValues.openhatPlayer = MediaPlayer.create(this, R.raw.openhat)
        ManeValues.scratchPlayer = MediaPlayer.create(this, R.raw.scratch)
        ManeValues.clapPlayer = MediaPlayer.create(this, R.raw.clap)
        ManeValues.cowbell1Player = MediaPlayer.create(this, R.raw.cowbell_long)
        ManeValues.cowbell2Player = MediaPlayer.create(this, R.raw.cowbell_long)
        ManeValues.cowbell3Player = MediaPlayer.create(this, R.raw.cowbell_long)

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
        btnEdit.setOnClickListener(menuManager)

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
            when(event?.actionMasked){
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN ->{
                    when (view?.id) {
                        R.id.btnKick -> {
                            playDrum(ManeValues.kickPlayer)
                        }

                        R.id.btnHihat -> {
                            playDrum(ManeValues.hihatPlayer)
                        }

                        R.id.btnSnare -> {
                            playDrum(ManeValues.snarePlayer)
                        }

                        R.id.btnOpenHat -> {
                            playDrum(ManeValues.openhatPlayer) //issue
                        }

                        R.id.btnScratch -> {
                            playDrum(ManeValues.scratchPlayer)
                        }

                        R.id.btnClap -> {
                            playDrum(ManeValues.clapPlayer)
                        }

                        R.id.btnCowbell1 -> {
                            playDrum(ManeValues.cowbell1Player)
                        }

                        R.id.btnCowbell2 -> {
                            playDrum(ManeValues.cowbell2Player)
                        }

                        R.id.btnCowbell3 -> {
                            playDrum(ManeValues.cowbell3Player)
                        }
                    }
                }
            }
            view?.performClick()
            return false
        }
    }

    private val menuManager = OnClickListener { view ->
        when(view.id){
            R.id.btnEdit -> {
                intent = Intent(this@RealTimeRhythm, RhythmicGridActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun playDrum(mediaPlayer: MediaPlayer){
        if(mediaPlayer.isPlaying){
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
        mediaPlayer.start()
    }

    private val myCheckedChangeListener =
        OnCheckedChangeListener { _, isChecked ->
            if (isChecked) metronomeStart()
            else metronomeStop()
        }

    private var timer: Timer? = null
    private var tickCount: Int = 0

    private fun metronomeStart() {
        timer = Timer()
        ManeValues.bpm =  editTextBpm.text.toString().toInt()
        val beatTime = (60_000 / ManeValues.bpm).toLong()
        timer?.scheduleAtFixedRate(timerTask {
            if(tickCount % 4 == 0) ManeValues.metronomeBeepDPlayer.start()
            else ManeValues.metronomeBeepCPlayer.start()
            tickCount++
        }, 0, beatTime)
    }

    private fun metronomeStop() {
        timer?.cancel()
        timer = null
        tickCount = 0
    }
}