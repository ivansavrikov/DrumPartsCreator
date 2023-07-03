package com.example.courseproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class DataViewModel : ViewModel() {
    val bpm: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val bars: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val stepsInBeat: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val isPlayed: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isPattern: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isSong: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isMetronome: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    private val _stepDuration: MutableLiveData<Long> by lazy { MutableLiveData<Long>() }
    val stepDuration : LiveData<Long> = _stepDuration

    fun changeBpm(bpm: Int, stepsInBeat: Int){
        _stepDuration.value = 60_000/bpm.toLong() / stepsInBeat
    }
}