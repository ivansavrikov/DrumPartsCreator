package com.example.courseproject

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.TextView
import android.widget.ToggleButton

class RhythmicGridActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rhythmic_grid)

        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        val rowCount = 16
        val columnCount = 1

        gridLayout.rowCount = rowCount
        gridLayout.columnCount = columnCount

        val columnSpec = GridLayout.spec(0, GridLayout.FILL, 1f)
        val rowSpec = GridLayout.spec(0, GridLayout.FILL, 1f)

        for (row in 0 until rowCount) {
            for (col in 0 until columnCount) {
                val cell = layoutInflater.inflate(R.layout.grid_cell, null) as FrameLayout

                val toggleButton = cell.findViewById<ToggleButton>(R.id.toggleButton)
                toggleButton.setOnCheckedChangeListener(myCheckedChangeListener)

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
            if (isChecked) view.setButtonDrawable(R.drawable.button_pressed)
            else view.setButtonDrawable(R.drawable.cell_rhythm_grid)
        }
}