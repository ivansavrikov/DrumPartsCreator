package com.example.courseproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import com.example.courseproject.core.ManeValues
import com.example.courseproject.core.Project808
import com.example.courseproject.database.DBManager
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json

class RhythmicGridActivity : AppCompatActivity() {
    private lateinit var btnSave: Button
    private lateinit var btnOpen: Button
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

        btnSave = findViewById(R.id.btnSave)
        btnOpen = findViewById(R.id.btnOpen)

        btnSave.setOnClickListener(onClick)
        btnOpen.setOnClickListener(onClick)

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
                playPadCutItself(ManeValues.currentPad, currentPatternIndex)
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

    private fun playPadCutItself(pad: Int, pool: Int){
        ManeValues.soundPools[pool].play(pad, 1.0f, 1.0f, 0, 0, 1.0f)
    }

    private fun playCurrentPattern() :Job {
        return CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY) {
            ensureActive()
            while (true){
                repeat(ManeValues.steps.size) {step ->
                    try {
                        buttonSteps[step].setBackgroundResource(R.drawable.step_button_played)
                        if (ManeValues.steps[step]) playPadCutItself(ManeValues.currentPad, currentPatternIndex)
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
                repeat(ManeValues.steps.size){step ->
                    try {
                        ManeValues.pads.forEachIndexed{ pattern, pad ->
                            if(ManeValues.patterns[pattern][step])
                                ManeValues.soundPools[pattern].play(pad, 1.0f, 1.0f, 0, 0, 1.0f)
                        }
                        buttonSteps[step].setBackgroundResource(R.drawable.step_button_played)
                        delay(ManeValues.stepDuration)
                        fillRhythmicStep(buttonSteps[step])
                    } finally {
                        fillRhythmicStep(buttonSteps[step])
                    }
                }
            }
        }
    }

    private val choosePattern = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            ManeValues.currentPad = ManeValues.pads[position]

            fillRhythmicPattern(ManeValues.patterns[position])

            ManeValues.steps.clear()
            ManeValues.steps.addAll(ManeValues.patterns[position])

            currentPatternIndex = position
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
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

    private fun fillRhythmicPattern(pattern: MutableList<Boolean>){
        buttonSteps.forEachIndexed { index, btnStep ->
            btnStep.setOnCheckedChangeListener(null) //hack
            btnStep.isChecked = pattern[index]
            btnStep.setOnCheckedChangeListener(setRhythmicStep)
            fillRhythmicStep(btnStep)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, RealTimeRhythm::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }

    private fun saveProject(title: String){
        val project = Project808(title, ManeValues.bpm, ManeValues.patterns)
        val jsonString = Json.encodeToString(Project808.serializer(), project)

        val dbManager = DBManager(this)
        dbManager.open()
        dbManager.insert(title, jsonString)
        dbManager.close()
    }

    private fun openProject(id: Int){
        val dbManager = DBManager(this)
        dbManager.open()

        val projectData = dbManager.getProject(id)
        val project = Json.decodeFromString<Project808>(projectData)

        ManeValues.bpm = project.bpm
        ManeValues.patterns = project.patterns

        dbManager.close()
    }

    private val onClick = OnClickListener { view ->
        when(view.id){
            R.id.btnSave -> {
                saveProject("Temp")
            }

            R.id.btnOpen -> {
                openProject(32)

                fillRhythmicPattern(ManeValues.patterns[currentPatternIndex])

                ManeValues.steps.clear()
                ManeValues.steps.addAll(ManeValues.patterns[currentPatternIndex])
            }
        }
    }
}