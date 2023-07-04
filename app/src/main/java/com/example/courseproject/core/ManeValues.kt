package com.example.courseproject.core

import android.media.AudioAttributes
import android.media.SoundPool

object ManeValues{
    internal var bpm: Int = 120 // beats per minute
    internal var beatDuration: Long = 500 //60_000/120
    internal var stepsInBeat: Int = 2 //Rhythmic grid step = 1/2
    internal var stepDuration :Long = 250 //beatDuration / stepInBeat

    internal var bars: Int = 4 // bars in rhythmic grid

    internal var currentPattern: MutableList<Boolean> = MutableList(size = 32) {false}

    internal var patterns: Array<MutableList<Boolean>> = arrayOf(
        MutableList(size = 32) {false},
        MutableList(size = 32) {false},
        MutableList(size = 32) {false},
        MutableList(size = 32) {false},
        MutableList(size = 32) {false},
        MutableList(size = 32) {false},
        MutableList(size = 32) {false},
        MutableList(size = 32) {false},
        MutableList(size = 32) {false},
        MutableList(size = 32) {false},
        MutableList(size = 32) {false},
        MutableList(size = 32) {false},
    )

    internal val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    internal val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(audioAttributes)
        .build()

    internal val SoundPoolMetronome = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(audioAttributes)
        .build()

    internal var soundPools = mutableListOf<SoundPool>()

    internal var pads = IntArray(12)
    internal var currentPad: Int = 0
}