package com.example.courseproject.core

import android.media.MediaPlayer
import com.example.courseproject.R
import com.example.courseproject.RhythmicGridActivity

object ManeValues{
    internal var bpm :Int = 120
    internal var beatDuration :Long = 250.toLong() //60_000/120
    internal val beats: MutableList<Boolean> = mutableListOf()

    internal lateinit var metronomePlayer: MediaPlayer
    internal lateinit var metronomeBeepDPlayer: MediaPlayer
    internal lateinit var metronomeBeepCPlayer: MediaPlayer
    internal lateinit var kickPlayer: MediaPlayer
    internal lateinit var hihatPlayer: MediaPlayer
    internal lateinit var snarePlayer: MediaPlayer
    internal lateinit var openhatPlayer: MediaPlayer
    internal lateinit var scratchPlayer: MediaPlayer
    internal lateinit var clapPlayer: MediaPlayer
    internal lateinit var cowbell1Player: MediaPlayer
    internal lateinit var cowbell2Player: MediaPlayer
    internal lateinit var cowbell3Player: MediaPlayer
}