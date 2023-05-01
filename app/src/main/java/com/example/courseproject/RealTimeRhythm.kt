package com.example.courseproject

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.ToggleButton

class RealTimeRhythm : AppCompatActivity(), OnCheckedChangeListener {

    private var bpm: Int = 80
    private var spanBetweenBeats: Int = 60000 / bpm

    private lateinit var metronome: ToggleButton
    private lateinit var mainHandler: Handler

    private lateinit var metronomePlayer: MediaPlayer
    private lateinit var kickPlayer: MediaPlayer
    private lateinit var hihatPlayer: MediaPlayer
    private lateinit var snarePlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_rhythm)

        metronomePlayer = MediaPlayer.create(this, R.raw.metronome)
        kickPlayer = MediaPlayer.create(this, R.raw.kick_1)
        hihatPlayer = MediaPlayer.create(this, R.raw.hihat_1)
        snarePlayer = MediaPlayer.create(this, R.raw.snare_1)

        metronome = findViewById(R.id.btnMetronome)
        metronome.setOnCheckedChangeListener(this)

        mainHandler = Handler(Looper.getMainLooper())
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnKick -> {
                kickPlayer.start()
            }

            R.id.btnHihat -> {
                hihatPlayer.start()
            }

            R.id.btnSnare -> {
                snarePlayer.start()
            }
        }
    }

    override fun onCheckedChanged(metronome: CompoundButton?, isChecked: Boolean) {
        if (isChecked) onResume()
        else onPause()
    }

    private val activateMetronome = object : Runnable {
        var beats: Int = 0
        override fun run() {
            beats++

            if (beats == 1 || beats % 4 == 0)
                metronomePlayer.setVolume(1.0f, 1.0f)
            else
                metronomePlayer.setVolume(0.7f, 0.7f)

            metronomePlayer.start()
            mainHandler.postDelayed(this, spanBetweenBeats.toLong())
        }
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(activateMetronome)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(activateMetronome)
    }
}