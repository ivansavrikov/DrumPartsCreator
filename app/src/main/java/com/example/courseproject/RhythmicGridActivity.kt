package com.example.courseproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import com.example.courseproject.core.ManeValues
import com.example.courseproject.core.Project808
import com.example.courseproject.database.DBManager
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.concurrent.timerTask

class RhythmicGridActivity : AppCompatActivity() {
    private lateinit var btnSave: Button
    private lateinit var btnOpen: Button
    private lateinit var btnMetronome: ToggleButton
    private var metronome: Int = 1

    private lateinit var btnPlayCurrentPattern: ToggleButton
    private lateinit var btnPlayAllPatterns: ToggleButton
    private lateinit var btnPlay: ToggleButton
    private var buttonSteps: MutableList<ToggleButton> = mutableListOf()
    private var currentPatternIndex: Int = 0

    private lateinit var editTextBpm: EditText


    private val mutex = Mutex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_rhythmic_grid)

        editTextBpm = findViewById(R.id.editTextBpm)
        editTextBpm.addTextChangedListener(onChangeBpm)

        btnMetronome = findViewById(R.id.btnMetronome)
//        btnMetronome.setOnCheckedChangeListener(myCheckedChangeListener)

        metronome = ManeValues.SoundPoolMetronome.load(this, R.raw.rim, 0)

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
        val pad10Name = resources.getString(R.string.pad10_name)
        val pad11Name = resources.getString(R.string.pad11_name)
        val pad12Name = resources.getString(R.string.pad12_name)

        val items = listOf(pad1Name, pad2Name, pad3Name, pad4Name, pad5Name, pad6Name, pad7Name, pad8Name, pad9Name, pad10Name, pad11Name, pad12Name) //idea
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        spinner.onItemSelectedListener = choosePattern
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        btnSave = findViewById(R.id.btnSave)
        btnOpen = findViewById(R.id.btnOpen)

        btnSave.setOnClickListener(onClick)
        btnOpen.setOnClickListener(onClick)


        btnPlay = findViewById(R.id.btnPlay)
        btnPlay.setOnCheckedChangeListener(performPlayback)

        btnPlayCurrentPattern = findViewById(R.id.btnPlayCurrentPattern)
        btnPlayCurrentPattern.setOnCheckedChangeListener(performPlayback)

        btnPlayAllPatterns = findViewById(R.id.btnPlayAllPatterns)
        btnPlayAllPatterns.setOnCheckedChangeListener(performPlayback)
        btnPlayAllPatterns.isChecked = true

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
        for(patternIndex in 0 until 12){
            ManeValues.patterns[patternIndex].addAll(ManeValues.steps)
        }
    }

    private val setRhythmicStep =
        CompoundButton.OnCheckedChangeListener { view, isChecked ->
            val step: Int = view.id
            ManeValues.steps[step] = isChecked
            ManeValues.patterns[currentPatternIndex][step] = isChecked

            if(view.isChecked && !btnPlay.isChecked)
                playPadCutItself(ManeValues.currentPad, currentPatternIndex)
            fillRhythmicStep(buttonSteps[step])
        }

    private var currentPlayback :Job? = null

    private fun playback(){
        currentPlayback?.cancel()
        if (btnPlay.isChecked){
            if(btnPlayCurrentPattern.isChecked && !btnPlayAllPatterns.isChecked)
                currentPlayback = playCurrentPattern()
            else if(btnPlayAllPatterns.isChecked && !btnPlayCurrentPattern.isChecked)
                currentPlayback = playAllPatterns()
            currentPlayback?.start()
        } else if(!btnPlay.isChecked){
            currentPlayback?.cancel()
        }
    }

    private val performPlayback =
        CompoundButton.OnCheckedChangeListener { view, isChecked ->
            when(view.id){

                R.id.btnPlay -> {
                    if(isChecked){
                        playback()
                    } else{
                        currentPlayback?.cancel()
                    }
                }

                R.id.btnPlayCurrentPattern -> {
                    if (isChecked){
                        btnPlayAllPatterns.isChecked = false
                        playback()
                    } else{
                        btnPlayAllPatterns.isChecked = true
                    }
                }
                R.id.btnPlayAllPatterns -> {
                    if (isChecked){
                        btnPlayCurrentPattern.isChecked = false
                        playback()
                    } else{
                        btnPlayCurrentPattern.isChecked = true
                    }
                }
            }
        }

    private fun playPadCutItself(pad: Int, pool: Int){
        ManeValues.soundPools[pool].play(pad, 1.0f, 1.0f, 0, 0, 1.0f)
    }

    private var startTime = System.currentTimeMillis() //начало воспроизведение ритмического шага
    private var endTime = System.currentTimeMillis() //конец воспроизведение ритмического шага
    private var totalTime = System.currentTimeMillis() //общее время воспроизведение ритмического шага


    private fun playCurrentPattern() :Job {
        return CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY) {
            mutex.withLock {
                ensureActive()
                while (true){
                    repeat(ManeValues.steps.size) {step ->
                        startTime = System.currentTimeMillis()
                        if(btnMetronome.isChecked && step % 2 == 0)
                            ManeValues.SoundPoolMetronome.play(metronome, 1.0f, 1.0f, 0, 0, 1.0f)
                        try {
                            buttonSteps[step].setBackgroundResource(R.drawable.step_button_played)
                            if (ManeValues.steps[step]) playPadCutItself(ManeValues.currentPad, currentPatternIndex)
                            endTime = System.currentTimeMillis()
                            totalTime = endTime - startTime
                            delay(ManeValues.stepDuration - totalTime)
                            fillRhythmicStep(buttonSteps[step])
                        } finally {
                            fillRhythmicStep(buttonSteps[step])
                        }
                    }
                }
            }
        }
    }

    private fun playAllPatterns() :Job {
        return CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY) {
            mutex.withLock {
                ensureActive()
                while (true){
                    repeat(ManeValues.steps.size){step ->
                        startTime = System.currentTimeMillis()
                        if(btnMetronome.isChecked && step % 2 == 0)
                            ManeValues.SoundPoolMetronome.play(metronome, 1.0f, 1.0f, 0, 0, 1.0f)
                        try {
                            ManeValues.pads.forEachIndexed{ pattern, pad ->
                                if(ManeValues.patterns[pattern][step])
                                    ManeValues.soundPools[pattern].play(pad, 1.0f, 1.0f, 0, 0, 1.0f)
                            }
                            buttonSteps[step].setBackgroundResource(R.drawable.step_button_played)
                            endTime = System.currentTimeMillis()
                            totalTime = endTime - startTime
                            delay(ManeValues.stepDuration - totalTime)
                            fillRhythmicStep(buttonSteps[step])
                        } finally {
                            fillRhythmicStep(buttonSteps[step])
                        }
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
        editTextBpm.setText(project.bpm.toString())
        ManeValues.patterns = project.patterns

        dbManager.close()
    }

    private val onClick = OnClickListener { view ->
        when(view.id){
            R.id.btnSave -> {
                saveProject("Temp")
            }

            R.id.btnOpen -> {
                openProject(1)

                fillRhythmicPattern(ManeValues.patterns[currentPatternIndex])

                ManeValues.steps.clear()
                ManeValues.steps.addAll(ManeValues.patterns[currentPatternIndex])
            }
        }
    }

    private val onChangeBpm = object : TextWatcher {
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

    override fun onPause() {
        super.onPause()
    }
    override fun onStop() {
        super.onStop()
        btnPlay.isChecked = false
        currentPlayback?.cancel()
        btnMetronome.isChecked = false
    }
    override fun onDestroy() {
        super.onDestroy()
        currentPlayback?.cancel()

        ManeValues.soundPools.forEach{soundPool ->
            soundPool.release()
        }
    }
}