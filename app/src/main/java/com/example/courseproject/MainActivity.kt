package com.example.courseproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.OnClickListener
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.courseproject.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var navHostFragment : NavHostFragment
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.navbar.setOnItemSelectedListener(navigationManager)
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

        }
        true
    }
}