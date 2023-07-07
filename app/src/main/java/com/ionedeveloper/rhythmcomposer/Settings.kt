package com.ionedeveloper.rhythmcomposer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.ionedeveloper.rhythmcomposer.databinding.FragmentDrumPadBinding
import com.ionedeveloper.rhythmcomposer.databinding.FragmentSettingsBinding
import com.ionedeveloper.rhythmcomposer.viewmodels.DataViewModel

class Settings : Fragment() {
    private val dataModel : DataViewModel by activityViewModels()

    private lateinit var binding : FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataModel.vibrateSetting.observe(activity as LifecycleOwner) {
            binding.cbVibrateSetting.isChecked = it
        }

        binding.cbVibrateSetting.setOnCheckedChangeListener { _, isChecked ->
            dataModel.vibrateSetting.value = isChecked
        }
    }
}