package com.example.courseproject

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import android.widget.CompoundButton.OnCheckedChangeListener
import java.util.*
import kotlin.concurrent.timerTask

class RealTimeRhythm : AppCompatActivity() {

    private lateinit var editTextBpm: EditText

    private lateinit var metronomePlayer: MediaPlayer
    private lateinit var metronomeBeepDPlayer: MediaPlayer
    private lateinit var metronomeBeepCPlayer: MediaPlayer
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

        editTextBpm = findViewById(R.id.editTextBpm)

        metronomePlayer = MediaPlayer.create(this, R.raw.metronome)
        metronomeBeepDPlayer = MediaPlayer.create(this, R.raw.metronome_beep_d)
        metronomeBeepCPlayer = MediaPlayer.create(this, R.raw.metronome_beep_c)

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
                            playDrum(kickPlayer)
                        }

                        R.id.btnHihat -> {
                            playDrum(hihatPlayer)
                        }

                        R.id.btnSnare -> {
                            playDrum(snarePlayer)
                        }

                        R.id.btnOpenHat -> {
                            playDrum(openhatPlayer) //issue
                        }

                        R.id.btnScratch -> {
                            playDrum(scratchPlayer)
                        }

                        R.id.btnClap -> {
                            playDrum(clapPlayer)
                        }

                        R.id.btnCowbell1 -> {
                            playDrum(cowbell1Player)
                        }

                        R.id.btnCowbell2 -> {
                            playDrum(cowbell2Player)
                        }

                        R.id.btnCowbell3 -> {
                            playDrum(cowbell3Player)
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
        val bpm: Int =  editTextBpm.text.toString().toInt()
        val beatTime = (60_000 / bpm).toLong()
        timer?.scheduleAtFixedRate(timerTask {
            if(tickCount % 4 == 0) metronomeBeepDPlayer.start()
            else metronomeBeepCPlayer.start()
            tickCount++
        }, 0, beatTime)
    }

    private fun metronomeStop() {
        timer?.cancel()
        timer = null
        tickCount = 0
    }
}