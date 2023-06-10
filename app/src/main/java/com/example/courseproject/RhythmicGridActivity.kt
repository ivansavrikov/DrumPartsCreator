package com.example.courseproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.courseproject.core.ManeValues
import kotlinx.coroutines.*

class RhythmicGridActivity : AppCompatActivity() {
    private lateinit var btnPlay: ToggleButton
    private lateinit var btnPlayParallel: ToggleButton
    private var previousSelectedPosition: Int = 0;
    private var buttonSteps: MutableList<ToggleButton> = mutableListOf()
    private var currentPatternIndex: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rhythmic_grid)

        val spinner: Spinner = findViewById(R.id.spinner)
        val pad1 = resources.getString(R.string.pad1_name)
        val pad2 = resources.getString(R.string.pad2_name)
        val pad3 = resources.getString(R.string.pad3_name)
        val pad4 = resources.getString(R.string.pad4_name)
        val pad5 = resources.getString(R.string.pad5_name)
        val pad6 = resources.getString(R.string.pad6_name)
        val pad7 = resources.getString(R.string.pad7_name)
        val pad8 = resources.getString(R.string.pad8_name)
        val pad9 = resources.getString(R.string.pad9_name)
        val items = listOf(pad1, pad2, pad3, pad4, pad5, pad6, pad7, pad8, pad9) //idea
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        spinner.onItemSelectedListener = choosePattern
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        btnPlay = findViewById(R.id.btnPlayCurrentPattern)
        btnPlay.setOnCheckedChangeListener(performPlayback)

        btnPlayParallel = findViewById(R.id.btnPlayAllPatterns)
        btnPlayParallel.setOnCheckedChangeListener(performPlayback)

        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        val rowCount = 8
        val columnCount = 1

        gridLayout.rowCount = rowCount
        gridLayout.columnCount = columnCount

        val columnSpec = GridLayout.spec(0, GridLayout.FILL, 1f)
        val rowSpec = GridLayout.spec(0, GridLayout.FILL, 1f)

        for (row in 0 until rowCount) {
            for (col in 0 until columnCount) {
                ManeValues.steps.add(row, false)
                val cell = layoutInflater.inflate(R.layout.grid_cell, null) as FrameLayout
                val toggleButton = cell.findViewById<ToggleButton>(R.id.toggleButton)

                val isOddBar: Boolean = ((row/8 + 1) % 2 != 0) //можно занести в отдельную функцию
                if(isOddBar) toggleButton.setBackgroundResource(R.drawable.step_button_off)
                else toggleButton.setBackgroundResource(R.drawable.step_button_off_v2)

                if(row % 2 == 0){
                    val tactNumber = ((row / 2) % 4 + 1).toString()
                    toggleButton.textOn = tactNumber
                    toggleButton.textOff = tactNumber
                    toggleButton.text = tactNumber //костыль мб
                }

                toggleButton.setOnCheckedChangeListener(setRhythmicStep)
                toggleButton.id = row
                buttonSteps.add(row, toggleButton)

                val layoutParams = GridLayout.LayoutParams(columnSpec, rowSpec)
                layoutParams.width = 0
                layoutParams.height = 0
                layoutParams.columnSpec = GridLayout.spec(col, 1f)
                layoutParams.rowSpec = GridLayout.spec(row, 1f)

                gridLayout.addView(cell, layoutParams)
            }
        }
        for(i in 0 until 9){
            ManeValues.patterns[i].addAll(ManeValues.steps)
        }
    }

    private val setRhythmicStep =
        CompoundButton.OnCheckedChangeListener { view, isChecked ->
            val step: Int = view.id
            ManeValues.steps[step] = isChecked
            ManeValues.patterns[currentPatternIndex][step] = isChecked

            if(view.isChecked) ManeValues.currentPlayer.start()
            fillRhythmicStep(buttonSteps[step])
        }

    private val performPlayback =
        CompoundButton.OnCheckedChangeListener { view, isChecked ->
            when(view.id){
                R.id.btnPlayCurrentPattern -> {
                    if (isChecked) {
                        CoroutineScope(Dispatchers.Default).launch {
                            repeat(ManeValues.steps.size) {step ->
                                ensureActive()
                                buttonSteps[step].setBackgroundResource(R.drawable.step_button_played)
                                if (ManeValues.steps[step]) ManeValues.currentPlayer.start()
                                delay(ManeValues.stepDuration)
                                fillRhythmicStep(buttonSteps[step])
                            }
                        }
                    } else {

                    }
                }
                R.id.btnPlayAllPatterns -> {
                    var currentPlayback = playAllPatterns()
                    if (isChecked){
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

    private fun playAllPatterns() :Job {
        return CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY) {
                repeat(ManeValues.steps.size) {step ->
                    ensureActive()
                    buttonSteps[step].setBackgroundResource(R.drawable.step_button_played)

                    startTime = System.currentTimeMillis() //testing

                    for(pattern in 0 until ManeValues.patterns.size){
                        if(ManeValues.patterns[pattern][step]){
                            ManeValues.players[pattern].start()
                        }
                    }

                    endTime = System.currentTimeMillis() //testing
                    totalTime = endTime - startTime //testing
                    Log.d("MyApp","Total time = $totalTime") //testing

                    delay(ManeValues.stepDuration)
                    fillRhythmicStep(buttonSteps[step])
                }
        }
    }

    private fun fillRhythmicStep(view: ToggleButton){
        if (view.isChecked) {
            view.setBackgroundResource(R.drawable.step_button_on)
        } else {
            val isOddBar: Boolean = ((view.id/8 + 1) % 2 != 0)
            if(isOddBar) view.setBackgroundResource(R.drawable.step_button_off)
            else view.setBackgroundResource(R.drawable.step_button_off_v2)
        }
    }

    private val choosePattern = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            ManeValues.currentPlayer = ManeValues.players[position]

            fillRhythmicPattern(ManeValues.patterns[position])

            ManeValues.patterns[previousSelectedPosition].clear()
            ManeValues.patterns[previousSelectedPosition].addAll(ManeValues.steps)

            ManeValues.steps.clear()
            ManeValues.steps.addAll(ManeValues.patterns[position])

            previousSelectedPosition = position
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