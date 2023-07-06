package com.ionedeveloper.rhythmcomposer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ionedeveloper.rhythmcomposer.core.CustomAdapterRecyclerView
import com.ionedeveloper.rhythmcomposer.core.ManeValues
import com.ionedeveloper.rhythmcomposer.core.Project808
import com.ionedeveloper.rhythmcomposer.database.DBManager
import com.ionedeveloper.rhythmcomposer.viewmodels.DataViewModel
import kotlinx.serialization.json.Json


class ProjectManager : Fragment(), CustomAdapterRecyclerView.ItemClickListener {
    private val dataModel : DataViewModel by activityViewModels()
    private var adapter: CustomAdapterRecyclerView? = null
    internal var projects :MutableList<String> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbManager = DBManager(requireContext())
        dbManager.open()
        val projectDictionary = dbManager.getAllProjects()
        for((key, value) in projectDictionary){
            projects.add(value)
        }
        dbManager.close()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_projects)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CustomAdapterRecyclerView(requireContext(), projects)
        adapter!!.setClickListener(this)
        recyclerView.adapter = adapter

    }

    override fun onItemClick(view: View?, position: Int) {
        openProject(position+1)
        val navController = findNavController()
        navController.navigate(R.id.sequencer)
    }

    private fun openProject(id: Int){
        val dbManager = DBManager(requireContext())
        dbManager.open()

        val projectData = dbManager.getProject(id)
        val project = Json.decodeFromString<Project808>(projectData)

        ManeValues.bpm = project.bpm
        dataModel.bpm.value = ManeValues.bpm
        ManeValues.stepsInBeat = project.stepsInBeat
        ManeValues.bars = project.bars

        ManeValues.patterns = project.patterns
        ManeValues.currentPattern.clear()

        dbManager.close()
    }
}