package com.example.courseproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ToggleButton
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.courseproject.core.ManeValues
import com.example.courseproject.databinding.FragmentDrumPadBinding
import com.example.courseproject.databinding.FragmentNewProjectBinding
import com.example.courseproject.viewmodels.DataViewModel
import com.shawnlin.numberpicker.NumberPicker

class NewProject : Fragment() {
    private val dataModel : DataViewModel by activityViewModels()
    private var _binding : FragmentNewProjectBinding? = null
    private val binding : FragmentNewProjectBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var navController : NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ManeValues.bpm = 120
        binding.barsPicker.value = 4
        binding.stepInBeatPicker.value = 2
        binding.barsPicker.setOnValueChangedListener(onChangeRhythmicGrid)
        binding.stepInBeatPicker.setOnValueChangedListener(onChangeRhythmicGrid)

        onChangeRhythmicGrid.onValueChange(binding.barsPicker, 0, 4)
        onChangeRhythmicGrid.onValueChange(binding.stepInBeatPicker, 0, 2)

        navController = findNavController()

        binding.btnCreate.setOnClickListener {
            navController.navigate(R.id.sequencer)
        }
    }

    private val onChangeRhythmicGrid =
        NumberPicker.OnValueChangeListener { picker, oldValue, newValue ->
            when(picker.id){
                R.id.bars_picker -> {
                    dataModel.bars.value = newValue
                    if(newValue != oldValue){
                        ManeValues.bars = newValue
                        fillRhythmicGrid(ManeValues.bars, ManeValues.stepsInBeat)
                    }
                }

                R.id.step_in_beat_picker -> {
                    dataModel.stepsInBeat.value = newValue
                    if(newValue != oldValue){
                        ManeValues.stepsInBeat = newValue
                        fillRhythmicGrid(ManeValues.bars, ManeValues.stepsInBeat)
                    }
                }
            }
        }

    private fun fillRhythmicGrid(bars: Int, stepsInBeat: Int){
        ManeValues.bars = bars
        ManeValues.stepsInBeat = stepsInBeat
        onResume()
    }

    override fun onResume() {
        super.onResume()

        ManeValues.currentPattern.clear()
        ManeValues.patterns.forEach { pattern ->
            pattern.clear()
        }

        val rowCount = ManeValues.bars
        val columnCount = ManeValues.stepsInBeat * 4

        binding.gridLayout.removeAllViews()
        binding.gridLayout.rowCount = rowCount
        binding.gridLayout.columnCount = columnCount

        val columnSpec = GridLayout.spec(0, GridLayout.FILL, 1f)
        val rowSpec = GridLayout.spec(0, GridLayout.FILL, 1f)

        var index: Int = 0
        for (row in 0 until rowCount) {
            for (col in 0 until columnCount) {
                ManeValues.currentPattern.add(index, false)
                val cell = layoutInflater.inflate(R.layout.grid_cell, null)
                val btnStep = cell.findViewById<ToggleButton>(R.id.toggleButton)
                btnStep.id = index
                fillRhythmicStep(btnStep)

                val layoutParams = GridLayout.LayoutParams(columnSpec, rowSpec)
                layoutParams.width = 0
                layoutParams.height = 0
                layoutParams.leftMargin = 2
                layoutParams.topMargin = 2
                layoutParams.rightMargin = 2
                layoutParams.bottomMargin = 2
                layoutParams.columnSpec = GridLayout.spec(col, 1f)
                layoutParams.rowSpec = GridLayout.spec(row, 1f)

                binding.gridLayout.addView(cell, layoutParams)
                index++
            }
        }
        for(patternIndex in 0 until 12){
            ManeValues.patterns[patternIndex].addAll(ManeValues.currentPattern)
        }
    }

    private fun fillRhythmicStep(step: ToggleButton){
        if(step.id % ManeValues.stepsInBeat == 0) step.setBackgroundResource(R.drawable.rhythmic_step_selector_v1_setting)
        else step.setBackgroundResource(R.drawable.rhythmic_step_selector_v2_setting)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}