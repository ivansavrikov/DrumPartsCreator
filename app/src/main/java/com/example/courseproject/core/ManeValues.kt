package com.example.courseproject.core

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool

object ManeValues{
    internal var bpm: Int = 120 // beats per minute
    internal var beatDuration: Long = 500 //60_000/120
    internal var step: Double = 0.5 //Rhythmic grid step = 1/2
    internal var stepDuration :Long = 250 //step * beatDuration

    internal var steps: MutableList<Boolean> = mutableListOf()

    internal var patterns: Array<MutableList<Boolean>> = arrayOf(
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf()
    )

    internal val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    internal val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(audioAttributes)
        .build()

    internal var soundPools = mutableListOf<SoundPool>()

    internal var pads = IntArray(9)
    internal var currentPad: Int = 0

    internal lateinit var metronomePlayer: MediaPlayer
    internal lateinit var metronomeBeepDPlayer: MediaPlayer
    internal lateinit var metronomeBeepCPlayer: MediaPlayer
}