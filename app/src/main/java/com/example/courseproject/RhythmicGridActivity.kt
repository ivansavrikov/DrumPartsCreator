package com.example.courseproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.courseproject.core.ManeValues
import kotlinx.coroutines.*
import android.media.MediaPlayer
import android.os.Build
import android.view.WindowInsets
import android.view.WindowInsetsController

class RhythmicGridActivity : AppCompatActivity() {
    private lateinit var btnPlayCurrentPattern: ToggleButton
    private lateinit var btnPlayAllPatterns: ToggleButton
    private var buttonSteps: MutableList<ToggleButton> = mutableListOf()
    private var currentPatternIndex: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        setContentView(R.layout.activity_rhythmic_grid)
        val spinner: Spinner = findViewById(R.id.spinner)
        val pad1Name = resources.getString(R.string.pad1_name)
        val pad2Name = resources.getString(R.string.pad2_name)
        val pad3Name = resources.getString(R.string.pad3_name)
        val pad4Name = resources.getString(R.string.pad4_name)
        val pad5Name = resources.getString(R.string.pad5_name)
        val pad6Name = resources.getString(R.string.pad6_name)
        val pad7Name = resources.getString(R.string.pad7_name)
        val pad8Name = resources.getString(R.string.pad8_name)
        val pad9Name = resources.getString(R.string.pad9_name)
        val items = listOf(pad1Name, pad2Name, pad3Name, pad4Name, pad5Name, pad6Name, pad7Name, pad8Name, pad9Name) //idea
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        spinner.onItemSelectedListener = choosePattern
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        btnPlayCurrentPattern = findViewById(R.id.btnPlayCurrentPattern)
        btnPlayCurrentPattern.setOnCheckedChangeListener(performPlayback)

        btnPlayAllPatterns = findViewById(R.id.btnPlayAllPatterns)
        btnPlayAllPatterns.setOnCheckedChangeListener(performPlayback)

        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        val rowCount = 4
        val columnCount = 8

        gridLayout.rowCount = rowCount
        gridLayout.columnCount = columnCount

        val columnSpec = GridLayout.spec(0, GridLayout.FILL, 1f)
        val rowSpec = GridLayout.spec(0, GridLayout.FILL, 1f)

        var index: Int = 0
        for (row in 0 until rowCount) {
            for (col in 0 until columnCount) {
                ManeValues.steps.add(index, false)
                val cell = layoutInflater.inflate(R.layout.grid_cell, null)

                val btnStep = cell.findViewById<ToggleButton>(R.id.toggleButton)

                fillRhythmicStep(btnStep)

                btnStep.setOnCheckedChangeListener(setRhythmicStep)
                btnStep.id = index
                buttonSteps.add(index, btnStep)

                val layoutParams = GridLayout.LayoutParams(columnSpec, rowSpec)
                layoutParams.width = 0
                layoutParams.height = 0
                layoutParams.columnSpec = GridLayout.spec(col, 1f)
                layoutParams.rowSpec = GridLayout.spec(row, 1f)

                gridLayout.addView(cell, layoutParams)
                index++
            }
        }
        for(patternIndex in 0 until 9){
            ManeValues.patterns[patternIndex].addAll(ManeValues.steps)
        }
    }

    private val setRhythmicStep =
        CompoundButton.OnCheckedChangeListener { view, isChecked ->
            val step: Int = view.id
            ManeValues.steps[step] = isChecked
            ManeValues.patterns[currentPatternIndex][step] = isChecked

            if(view.isChecked && !btnPlayAllPatterns.isChecked && !btnPlayCurrentPattern.isChecked)
                playPadCutItself(ManeValues.currentPlayer)
            fillRhythmicStep(buttonSteps[step])
        }

    private lateinit var currentPlayback :Job

    private val performPlayback =
        CompoundButton.OnCheckedChangeListener { view, isChecked ->
            when(view.id){
                R.id.btnPlayCurrentPattern -> {
                    if (isChecked){
                        currentPlayback = playCurrentPattern()
                        currentPlayback.start()
                    } else {
                        currentPlayback.cancel()
                    }
                }
                R.id.btnPlayAllPatterns -> {
                    if (isChecked){
                        currentPlayback = playAllPatterns()
                        currentPlayback.start()
                    } else {
                        currentPlayback.cancel()
                    }
                }
            }
        }

    private var startTime = System.currentTimeMillis() //testing
    private var endTime = System.currentTimeMillis() //testing
    private var totalTime = System.currentTimeMillis() //testing

    private fun playPadCutItself(pad: MediaPlayer){ //hack
        if(pad.isPlaying){
            pad.pause()
            pad.seekTo(0)
        }
        pad.start()
    }

    private fun playCurrentPattern() :Job {
        return CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY) {
            ensureActive()
            while (true){
                repeat(ManeValues.steps.size) {step ->
                    try {
                        buttonSteps[step].setBackgroundResource(R.drawable.step_button_played)
                        if (ManeValues.steps[step]) playPadCutItself(ManeValues.currentPlayer)
                        delay(ManeValues.stepDuration)
                        fillRhythmicStep(buttonSteps[step])
                    } finally {
                        fillRhythmicStep(buttonSteps[step])
                    }
                }
            }
        }
    }

    private fun playAllPatterns() :Job {
        return CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY) {
            ensureActive()
            while (true){
                repeat(ManeValues.steps.size) {step ->
                    try {
                        startTime = System.currentTimeMillis() //testing
                        buttonSteps[step].setBackgroundResource(R.drawable.step_button_played)

                        for(pattern in 0 until ManeValues.patterns.size){
                            if(ManeValues.patterns[pattern][step]){
                                if(ManeValues.players[pattern].isPlaying){
                                    ManeValues.players[pattern].pause()
                                    ManeValues.players[pattern].seekTo(0)
                                }
                                endTime = System.currentTimeMillis() //testing
                                totalTime = endTime - startTime //testing
                                Log.d("MyApp","test before start sound time = $totalTime") //testing
                                ManeValues.players[pattern].start()
                            }
                        }

                        endTime = System.currentTimeMillis() //testing
                        totalTime = endTime - startTime //testing
                        Log.d("MyApp","test1 = $totalTime") //testing

                        delay(ManeValues.stepDuration - totalTime) //200 iq move

                        endTime = System.currentTimeMillis() //testing
                        totalTime = endTime - startTime //testing
                        Log.d("MyApp","test2 = $totalTime") //testing
                        fillRhythmicStep(buttonSteps[step])
                    } finally {
                        fillRhythmicStep(buttonSteps[step])
                    }
                }
            }
        }
    }

    private fun fillRhythmicStep(view: ToggleButton){
        if (view.isChecked) {
            view.setBackgroundResource(R.drawable.step_button_on)
        } else {
            if(view.id % 2 == 0) view.setBackgroundResource(R.drawable.step_button_off_v2)
            else view.setBackgroundResource(R.drawable.step_button_off)
        }
    }

    private val choosePattern = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            ManeValues.currentPlayer = ManeValues.players[position]

            fillRhythmicPattern(ManeValues.patterns[position])

            ManeValues.steps.clear()
            ManeValues.steps.addAll(ManeValues.patterns[position])

            currentPatternIndex = position
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    private fun fillRhythmicPattern(pattern: MutableList<Boolean>){
        buttonSteps.forEachIndexed { index, btnStep ->
            btnStep.setOnCheckedChangeListener(null) //hack
            btnStep.isChecked = pattern[index]
            btnStep.setOnCheckedChangeListener(setRhythmicStep)
            fillRhythmicStep(btnStep)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, RealTimeRhythm::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }
}