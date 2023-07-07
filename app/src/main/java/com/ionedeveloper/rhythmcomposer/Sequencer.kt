package com.ionedeveloper.rhythmcomposer

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.ionedeveloper.rhythmcomposer.core.ManeValues
import com.ionedeveloper.rhythmcomposer.core.Project808
import com.ionedeveloper.rhythmcomposer.database.DBManager
import com.ionedeveloper.rhythmcomposer.databinding.FragmentSequencerBinding
import com.shawnlin.numberpicker.NumberPicker
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

class Sequencer : Fragment() {
    private lateinit var binding : FragmentSequencerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentSequencerBinding.inflate(inflater)
        return binding.root
    }

    lateinit var titlesPatterns : List<String>
    private var metronome: Int = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rhythmicButtons.clear()

        val rowCount = ManeValues.bars
        val columnCount = ManeValues.stepsInBeat * 4

        binding.gridLayout.rowCount = rowCount
        binding.gridLayout.columnCount = columnCount

        val columnSpec = GridLayout.spec(0, GridLayout.FILL, 1f)
        val rowSpec = GridLayout.spec(0, GridLayout.FILL, 1f)

        var index: Int = 0
        for (row in 0 until rowCount) {
            for (col in 0 until columnCount) {
                val rhythmicStep = layoutInflater.inflate(R.layout.grid_cell, null)
                val rhythmicButton = rhythmicStep.findViewById<ToggleButton>(R.id.toggleButton)

                rhythmicButton.setOnCheckedChangeListener(setRhythmicStep)
                rhythmicButton.id = index

                fillRhythmicStep(rhythmicButton)

                rhythmicButtons.add(index, rhythmicButton)

                val layoutParams = GridLayout.LayoutParams(columnSpec, rowSpec)
                layoutParams.width = 0
                layoutParams.height = 0
                layoutParams.leftMargin = 2
                layoutParams.topMargin = 2
                layoutParams.rightMargin = 2
                layoutParams.bottomMargin = 2
                layoutParams.columnSpec = GridLayout.spec(col, 1f)
                layoutParams.rowSpec = GridLayout.spec(row, 1f)

                binding.gridLayout.addView(rhythmicStep, layoutParams)
                index++
            }
        }

        fillRhythmicPattern(ManeValues.patterns[currentPatternIndex])

        metronome = ManeValues.SoundPoolMetronome.load(requireContext(), R.raw.metronome_strong, 0)

        val spinner: Spinner = view.findViewById(R.id.spinner)

        titlesPatterns = listOf(
            resources.getString(R.string.pad1_name),
            resources.getString(R.string.pad2_name),
            resources.getString(R.string.pad3_name),
            resources.getString(R.string.pad4_name),
            resources.getString(R.string.pad5_name),
            resources.getString(R.string.pad6_name),
            resources.getString(R.string.pad7_name),
            resources.getString(R.string.pad8_name),
            resources.getString(R.string.pad9_name),
            resources.getString(R.string.pad10_name),
            resources.getString(R.string.pad11_name),
            resources.getString(R.string.pad12_name)
        )

        spinner.onItemSelectedListener = choosePattern

        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, titlesPatterns)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        binding.btnSave.setOnClickListener(onClick)

        val navController = findNavController()
        binding.btnNewProject.setOnClickListener {
            navController.navigate(R.id.newProject)
        }

        binding.btnClearPattern.setOnClickListener(clearPattern)
        binding.btnPlay.setOnCheckedChangeListener(performPlayback)
        binding.btnPlayCurrentPattern.setOnCheckedChangeListener(performPlayback)
        binding.btnPlayAllPatterns.setOnCheckedChangeListener(performPlayback)
        binding.btnPlayAllPatterns.isChecked = true
        binding.bpmPicker.setOnValueChangedListener(onChangeBpm)
        binding.bpmPicker.value = ManeValues.bpm
        onChangeBpm.onValueChange(binding.bpmPicker, 0, ManeValues.bpm)

        binding.btnOkay.setOnClickListener(onClick)
    }

    private var rhythmicButtons: MutableList<ToggleButton> = mutableListOf()

    private var currentPatternIndex: Int = 0
    private val mutex = Mutex()

    private val setRhythmicStep =
        CompoundButton.OnCheckedChangeListener { view, isChecked ->
            val step: Int = view.id
            ManeValues.currentPattern[step] = isChecked
            ManeValues.patterns[currentPatternIndex][step] = isChecked

            if(view.isChecked && !binding.btnPlay.isChecked)
                playPadCutItself(ManeValues.currentPad, currentPatternIndex)
            fillRhythmicStep(rhythmicButtons[step])
        }

    private var currentPlayback : Job? = null

    private fun playback(){
        currentPlayback?.cancel()
        if (binding.btnPlay.isChecked){
            if(binding.btnPlayCurrentPattern.isChecked && !binding.btnPlayAllPatterns.isChecked)
                currentPlayback = playCurrentPattern()
            else if(binding.btnPlayAllPatterns.isChecked && !binding.btnPlayCurrentPattern.isChecked)
                currentPlayback = playAllPatterns()
            currentPlayback?.start()
        } else if(!binding.btnPlay.isChecked){
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
                        binding.btnPlayAllPatterns.isChecked = false
                        playback()
                    } else{
                        binding.btnPlayAllPatterns.isChecked = true
                    }
                }
                R.id.btnPlayAllPatterns -> {
                    if (isChecked){
                        binding.btnPlayCurrentPattern.isChecked = false
                        playback()
                    } else{
                        binding.btnPlayCurrentPattern.isChecked = true
                    }
                }
            }
        }

    private var volume: Float = 1.0f
    private var rate: Float = 1.0f
    private var priority: Int = 0
    private var loop: Int = 0
    private fun playPadCutItself(pad: Int, pool: Int){
        ManeValues.soundPools[pool].play(pad, volume, volume, priority, loop, rate)
    }

    private var startTime = System.currentTimeMillis() //начало воспроизведение ритмического шага
    private var endTime = System.currentTimeMillis() //конец воспроизведение ритмического шага
    private var totalTime = System.currentTimeMillis() //общее время воспроизведение ритмического шага


    private fun playCurrentPattern() : Job { //функция для воспроизведения текущего паттерна
        return CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY) {//запускаем коррутину
            mutex.withLock {
                ensureActive() // проверяем активна ли данная коррутина
                while (true){
                    repeat(ManeValues.currentPattern.size) { step -> //перебираем индексы массива кнопок
                        startTime = System.currentTimeMillis()
                        if(binding.btnMetronome.isChecked && step % ManeValues.stepsInBeat == 0)
                            ManeValues.SoundPoolMetronome.play(metronome, volume, volume, priority, loop, rate)
                        try {
                            if (ManeValues.currentPattern[step]) playPadCutItself(ManeValues.currentPad, currentPatternIndex)
                            withContext(Dispatchers.Main) {
                                rhythmicButtons[step].setBackgroundResource(R.drawable.rhythmic_step_played)
                            }
                            endTime = System.currentTimeMillis()
                            totalTime = endTime - startTime
                            delay(ManeValues.stepDuration - totalTime)
                            withContext(Dispatchers.Main) {
                                fillRhythmicStep(rhythmicButtons[step])
                            }
                        } finally {
                            fillRhythmicStep(rhythmicButtons[step])
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
                    repeat(ManeValues.currentPattern.size){ step ->
                        startTime = System.currentTimeMillis()
                        if(binding.btnMetronome.isChecked && step % ManeValues.stepsInBeat == 0)
                            ManeValues.SoundPoolMetronome.play(metronome, 1.0f, 1.0f, 0, 0, 1.0f)
                        try {
                            ManeValues.pads.forEachIndexed{ pattern, pad ->
                                if(ManeValues.patterns[pattern][step])
                                    ManeValues.soundPools[pattern].play(pad, 1.0f, 1.0f, 0, 0, 1.0f)
                            }

                            withContext(Dispatchers.Main) {
                                rhythmicButtons[step].setBackgroundResource(R.drawable.rhythmic_step_played)
                            }

                            endTime = System.currentTimeMillis()
                            totalTime = endTime - startTime
                            delay(ManeValues.stepDuration - totalTime)

                            withContext(Dispatchers.Main) {
                                fillRhythmicStep(rhythmicButtons[step])
                            }
                        } finally {
                            fillRhythmicStep(rhythmicButtons[step])
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

            ManeValues.currentPattern.clear()
            ManeValues.currentPattern.addAll(ManeValues.patterns[position])

            currentPatternIndex = position
            binding.btnPlayCurrentPattern.text = titlesPatterns[currentPatternIndex]
            binding.btnPlayCurrentPattern.textOn = titlesPatterns[currentPatternIndex]
            binding.btnPlayCurrentPattern.textOff = titlesPatterns[currentPatternIndex]
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun fillRhythmicStep(step: ToggleButton){
        if(step.id % ManeValues.stepsInBeat == 0) step.setBackgroundResource(R.drawable.rhythmic_step_selector_v1)
        else step.setBackgroundResource(R.drawable.rhythmic_step_selector_v2)
    }

    private fun fillRhythmicPattern(pattern: MutableList<Boolean>){
        rhythmicButtons.forEachIndexed { index, btnStep ->
            btnStep.setOnCheckedChangeListener(null)
            btnStep.isChecked = pattern[index]
            btnStep.setOnCheckedChangeListener(setRhythmicStep)
            fillRhythmicStep(btnStep)
        }
    }

    private fun saveProject(title: String){
        val project = Project808(title, ManeValues.bpm, ManeValues.bars, ManeValues.stepsInBeat, ManeValues.patterns)
        val jsonString = Json.encodeToString(Project808.serializer(), project)

        val dbManager = DBManager(requireContext())
        dbManager.open()
        dbManager.insert(title, jsonString)
        dbManager.close()
    }

    private val onClick = View.OnClickListener { view ->
        when (view.id) {
            R.id.btnSave -> {
                binding.etProjectTitle.visibility = View.VISIBLE
                binding.btnOkay.visibility = View.VISIBLE
                binding.etProjectTitle.requestFocus()
            }
            R.id.btnOkay -> {
                var title = binding.etProjectTitle.text.toString()
                if(title == "") title = "untitled"
                saveProject(title)
                Toast.makeText(requireContext(), "Проект $title сохранен", Toast.LENGTH_SHORT).show()
                binding.etProjectTitle.visibility = View.INVISIBLE
                binding.btnOkay.visibility = View.INVISIBLE
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.etProjectTitle.windowToken, 0)
            }
        }
    }

    private val clearPattern = View.OnClickListener {
        ManeValues.patterns[currentPatternIndex].fill(false)
        ManeValues.currentPattern.clear()
        ManeValues.currentPattern.addAll(ManeValues.patterns[currentPatternIndex])

        fillRhythmicPattern(ManeValues.patterns[currentPatternIndex])
    }

    private val onChangeBpm = NumberPicker.OnValueChangeListener { _, _, bpm ->
        ManeValues.bpm = bpm
        ManeValues.beatDuration = 60_000/ ManeValues.bpm.toLong()
        ManeValues.stepDuration = ManeValues.beatDuration/ ManeValues.stepsInBeat
    }

    override fun onPause() {
        super.onPause()
        currentPlayback?.cancel()
    }
    override fun onStop() {
        super.onStop()
        currentPlayback?.cancel()
        binding.btnPlay.isChecked = false
    }
    override fun onDestroy() {
        super.onDestroy()
        currentPlayback?.cancel()
    }
}