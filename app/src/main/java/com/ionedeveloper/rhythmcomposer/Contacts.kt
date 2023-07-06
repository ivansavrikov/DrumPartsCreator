package com.ionedeveloper.rhythmcomposer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ionedeveloper.rhythmcomposer.databinding.FragmentContactsBinding

class Contacts : Fragment() {
    private lateinit var binding : FragmentContactsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:ivansavrikow@gmail.com")
            startActivity(emailIntent)
        }

        binding.ivGitHub.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://github.com/AGHETTOCHRISTMASCAROL/808MachineVersions")
            startActivity(intent)
        }
    }
}