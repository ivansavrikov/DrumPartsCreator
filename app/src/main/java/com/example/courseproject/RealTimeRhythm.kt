package com.example.courseproject

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class RealTimeRhythm : AppCompatActivity(){

    private lateinit var snare: Button
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_rhythm)

        snare = findViewById(R.id.btnSnare)
    }

    fun onClick(view: View){
        when(view.id){
            R.id.btnKick -> {
                mediaPlayer = MediaPlayer.create(this, R.raw.kick_1)
                mediaPlayer.isPlaying
                mediaPlayer.start()
            }

            R.id.btnSnare -> {
                mediaPlayer = MediaPlayer.create(this, R.raw.snare_1)
                mediaPlayer.isPlaying
                mediaPlayer.start()
            }

            R.id.btnHihat -> {
                mediaPlayer = MediaPlayer.create(this, R.raw.hihat_1)
                mediaPlayer.isPlaying
                mediaPlayer.start()
            }
        }
    }
}