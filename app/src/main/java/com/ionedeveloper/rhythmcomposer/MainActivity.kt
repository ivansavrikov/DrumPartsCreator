package com.ionedeveloper.rhythmcomposer

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ionedeveloper.rhythmcomposer.core.ManeValues
import com.ionedeveloper.rhythmcomposer.databinding.ActivityMainBinding
import com.ionedeveloper.rhythmcomposer.viewmodels.DataViewModel
import com.google.android.material.navigation.NavigationBarView
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val dataModel : DataViewModel by viewModels()

    private lateinit var navHostFragment : NavHostFragment
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.navbar.setOnItemSelectedListener(navigationManager)

        for(i in 0 until 12){
            val soundPool = SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(ManeValues.audioAttributes)
                .build()
            ManeValues.soundPools.add(i, soundPool)
        }

        ManeValues.pads[0] = ManeValues.soundPools[0].load(this, R.raw.kick, 0)
        ManeValues.pads[1] = ManeValues.soundPools[1].load(this, R.raw.hihat, 0)
        ManeValues.pads[2] = ManeValues.soundPools[2].load(this, R.raw.snare, 0)
        ManeValues.pads[3] = ManeValues.soundPools[3].load(this, R.raw.openhat, 0)
        ManeValues.pads[4] = ManeValues.soundPools[4].load(this, R.raw.rim, 0)
        ManeValues.pads[5] = ManeValues.soundPools[5].load(this, R.raw.clap, 0)
        ManeValues.pads[6] = ManeValues.soundPools[6].load(this, R.raw.cowbell, 0)
        ManeValues.pads[7] = ManeValues.soundPools[7].load(this, R.raw.snap, 0)
        ManeValues.pads[8] = ManeValues.soundPools[8].load(this, R.raw.bass_808, 0)
        ManeValues.pads[9] = ManeValues.soundPools[9].load(this, R.raw.bass_zaytoven, 0)
        ManeValues.pads[10] = ManeValues.soundPools[10].load(this, R.raw.bass_suge, 0)
        ManeValues.pads[11] = ManeValues.soundPools[11].load(this, R.raw.scratch, 0)
    }

    private val navigationManager = NavigationBarView.OnItemSelectedListener { item ->
        when (item.itemId) {

            R.id.drumPad -> {
                navController.navigate(R.id.drumPad)
            }

            R.id.rhythmicGrid -> {
                navController.navigate(R.id.sequencer)
            }

            R.id.projects -> {
                navController.navigate(R.id.projectManager)
            }

            R.id.contacts -> {
                navController.navigate(R.id.contacsFragment)
            }

        }
        true
    }

    override fun onDestroy() {
        super.onDestroy()
        ManeValues.soundPools.forEach{soundPool ->
            soundPool.release()
        }
        ManeValues.SoundPoolMetronome.release()
    }
}