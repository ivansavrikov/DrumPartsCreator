package com.example.courseproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.courseproject.core.ManeValues

class RhythmicGridActivity : AppCompatActivity() {
    private lateinit var btnPlay: ToggleButton
    private var buttonSteps: MutableList<ToggleButton> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rhythmic_grid)

        btnPlay = findViewById(R.id.btnPlay)
        btnPlay.setOnCheckedChangeListener(performPlayback)
        Toast.makeText(this, "temp", Toast.LENGTH_SHORT).show()

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
                toggleButton.setOnCheckedChangeListener(onClickOnStep)
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

        ManeValues.currentPlayer = ManeValues.pad2Player
    }

    private val onClickOnStep =
        CompoundButton.OnCheckedChangeListener { view, isChecked ->
            val step :Int = view.id
            ManeValues.steps[step] = isChecked

            if (isChecked){
                ManeValues.currentPlayer.start()
                view.setButtonDrawable(R.drawable.button_enabled)
            }
            else view.setButtonDrawable(R.drawable.cell_rhythm_grid)
        }

    private val performPlayback = //Функция воспроизведения паттерна
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                for (step in ManeValues.steps){ //Небольшой конфликт имен
                    if (step) ManeValues.currentPlayer.start()
                    Thread.sleep(ManeValues.stepDuration)
                }
            }
        }

    override fun onBackPressed(){
        val intent = Intent(this, RealTimeRhythm::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }
}