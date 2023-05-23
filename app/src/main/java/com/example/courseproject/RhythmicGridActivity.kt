package com.example.courseproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ToggleButton
import com.example.courseproject.core.ManeValues

class RhythmicGridActivity : AppCompatActivity() {
    private lateinit var btnPlay: ToggleButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rhythmic_grid)

        btnPlay = findViewById(R.id.btnPlay)
        btnPlay.setOnCheckedChangeListener(performPlayback)

        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        val rowCount = 16
        val columnCount = 1

        gridLayout.rowCount = rowCount
        gridLayout.columnCount = columnCount

        val columnSpec = GridLayout.spec(0, GridLayout.FILL, 1f)
        val rowSpec = GridLayout.spec(0, GridLayout.FILL, 1f)

        for (row in 0 until rowCount) {
            for (col in 0 until columnCount) {
                ManeValues.beats.add(row, false)
                val cell = layoutInflater.inflate(R.layout.grid_cell, null) as FrameLayout

                val toggleButton = cell.findViewById<ToggleButton>(R.id.toggleButton)
                toggleButton.setOnCheckedChangeListener(myCheckedChangeListener)
                toggleButton.id = row

                val layoutParams = GridLayout.LayoutParams(columnSpec, rowSpec)
                layoutParams.width = 0
                layoutParams.height = 0
                layoutParams.columnSpec = GridLayout.spec(col, 1f)
                layoutParams.rowSpec = GridLayout.spec(row, 1f)

                gridLayout.addView(cell, layoutParams)
            }
        }
    }

    private val myCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { view, isChecked ->
            val beat :Int = view.id
            ManeValues.beats[beat] = isChecked

            if (isChecked) view.setButtonDrawable(R.drawable.button_enabled)
            else view.setButtonDrawable(R.drawable.cell_rhythm_grid)
        }

    private val performPlayback = //Функция воспроизведения паттерна
        CompoundButton.OnCheckedChangeListener { view, isChecked ->
            if(isChecked){
                for (beat in ManeValues.beats){
                    if (beat) ManeValues.hihatPlayer.start()
//                    else{
//                        ManeValues.hihatPlayer.pause()
//                        ManeValues.hihatPlayer.seekTo(0)
//                    }
                    Thread.sleep(ManeValues.beatDuration)
                }
            }
        }
}