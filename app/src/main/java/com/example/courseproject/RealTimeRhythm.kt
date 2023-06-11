package com.example.courseproject

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        setContentView(R.layout.activity_real_time_rhythm)

        editTextBpm = findViewById(R.id.editTextBpm)
        editTextBpm.addTextChangedListener(onChangeBpm)

        ManeValues.metronomeBeepDPlayer = MediaPlayer.create(this, R.raw.metronome_beep_d) //temp maybe
        ManeValues.metronomeBeepCPlayer = MediaPlayer.create(this, R.raw.metronome_beep_c)

        val pad1Sound: Uri = Uri.parse("android.resource://com.example.courseproject/raw/kick_mixed")
        val pad2Sound: Uri = Uri.parse("android.resource://com.example.courseproject/raw/hihat_1")
        val pad3Sound: Uri = Uri.parse("android.resource://com.example.courseproject/raw/snare_1")
        val pad4Sound: Uri = Uri.parse("android.resource://com.example.courseproject/raw/openhat")
        val pad5Sound: Uri = Uri.parse("android.resource://com.example.courseproject/raw/scratch")
        val pad6Sound: Uri = Uri.parse("android.resource://com.example.courseproject/raw/clap")
        val pad7Sound: Uri = Uri.parse("android.resource://com.example.courseproject/raw/cowbell_long")
        val pad8Sound: Uri = Uri.parse("android.resource://com.example.courseproject/raw/cowbell_long")
        val pad9Sound: Uri = Uri.parse("android.resource://com.example.courseproject/raw/cowbell_long")

        ManeValues.players.add(MediaPlayer.create(this, pad1Sound))
        ManeValues.players.add(MediaPlayer.create(this, pad2Sound))
        ManeValues.players.add(MediaPlayer.create(this, pad3Sound))
        ManeValues.players.add(MediaPlayer.create(this, pad4Sound))
        ManeValues.players.add(MediaPlayer.create(this, pad5Sound))
        ManeValues.players.add(MediaPlayer.create(this, pad6Sound))
        ManeValues.players.add(MediaPlayer.create(this, pad7Sound))
        ManeValues.players.add(MediaPlayer.create(this, pad8Sound))
        ManeValues.players.add(MediaPlayer.create(this, pad9Sound))

        val pad1 = ManeValues.soundPool.load(this, R.raw.kick_mixed, 0)
        val pad2 = ManeValues.soundPool.load(this, R.raw.hihat_1, 0)
        val pad3 = ManeValues.soundPool.load(this, R.raw.snare_1, 0)
        val pad4 = ManeValues.soundPool.load(this, R.raw.openhat, 1)
        val pad5 = ManeValues.soundPool.load(this, R.raw.scratch, 1)
        val pad6 = ManeValues.soundPool.load(this, R.raw.clap, 0)
        val pad7 = ManeValues.soundPool.load(this, R.raw.cowbell_long, 1)
        val pad8 = ManeValues.soundPool.load(this, R.raw.cowbell_long, 1)
        val pad9 = ManeValues.soundPool.load(this, R.raw.cowbell_long, 1)

        ManeValues.soundPool.play(pad1, 1.0f, 1.0f, 1, 0, 1.0f)

        val btnMetronome: ToggleButton = findViewById(R.id.btnMetronome)
        btnMetronome.setOnCheckedChangeListener(myCheckedChangeListener)

        val btnPad1: Button = findViewById(R.id.btnPad1)
        val btnPad2: Button = findViewById(R.id.btnPad2)
        val btnPad3: Button = findViewById(R.id.btnPad3)
        val btnPad4 :Button = findViewById(R.id.btnPad4)
        val btnPad5 :Button = findViewById(R.id.btnPad5)
        val btnPad6 :Button = findViewById(R.id.btnPad6)
        val btnPad7 :Button = findViewById(R.id.btnPad7)
        val btnPad8 :Button = findViewById(R.id.btnPad8)
        val btnPad9 :Button = findViewById(R.id.btnPad9)

        val btnEdit: Button = findViewById(R.id.btnEdit)
        btnEdit.setOnClickListener(menuManager)

        btnPad1.setOnTouchListener(drumTouchListener)
        btnPad2.setOnTouchListener(drumTouchListener)
        btnPad3.setOnTouchListener(drumTouchListener)
        btnPad4.setOnTouchListener(drumTouchListener)
        btnPad5.setOnTouchListener(drumTouchListener)
        btnPad6.setOnTouchListener(drumTouchListener)
        btnPad7.setOnTouchListener(drumTouchListener)
        btnPad8.setOnTouchListener(drumTouchListener)
        btnPad9.setOnTouchListener(drumTouchListener)
    }

    private val drumTouchListener = object : View.OnTouchListener {
        override fun onTouch(view: View?, event: MotionEvent?): Boolean {
            when(event?.actionMasked){
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN ->{
                    when (view?.id) {
                        R.id.btnPad1 -> {
                            playDrumCutItself(ManeValues.players[0])
                        }

                        R.id.btnPad2 -> {
                            playDrumCutItself(ManeValues.players[1])
                        }

                        R.id.btnPad3 -> {
                            playDrumCutItself(ManeValues.players[2])
                        }

                        R.id.btnPad4 -> {
                            playDrumCutItself(ManeValues.players[3]) //issue
                        }

                        R.id.btnPad5 -> {
                            playDrumCutItself(ManeValues.players[4])
                        }

                        R.id.btnPad6 -> {
                            playDrumCutItself(ManeValues.players[5])
                        }

                        R.id.btnPad7 -> {
                            playDrumCutItself(ManeValues.players[6])
                        }

                        R.id.btnPad8 -> {
                            playDrumCutItself(ManeValues.players[7])
                        }

                        R.id.btnPad9 -> {
                            playDrumCutItself(ManeValues.players[8])
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
                val intent = Intent(this, RhythmicGridActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
        }
    }

    private fun playDrumCutItself(mediaPlayer: MediaPlayer){ //hack
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

    private val onChangeBpm = object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            return
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            return
        }

        override fun afterTextChanged(editText: Editable) {
            if(editTextBpm.text.toString() != ""){ //hack
                ManeValues.bpm = editTextBpm.text.toString().toInt()
                ManeValues.beatDuration = 60_000/ManeValues.bpm.toLong()
                ManeValues.stepDuration = ManeValues.beatDuration/2
            }
        }
    }

    private var timer: Timer? = null
    private var tickCount: Int = 0

    private fun metronomeStart() {
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            if(tickCount % 4 == 0) ManeValues.metronomeBeepDPlayer.start()
            else ManeValues.metronomeBeepCPlayer.start()
            tickCount++
        }, 0, ManeValues.beatDuration)
    }

    private fun metronomeStop() {
        timer?.cancel()
        timer = null
        tickCount = 0
    }
}