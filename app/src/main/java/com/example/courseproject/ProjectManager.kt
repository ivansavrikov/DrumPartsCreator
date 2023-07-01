package com.example.courseproject
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.courseproject.core.CustomAdapterRecyclerView
import com.example.courseproject.database.DBManager


class ProjectManager : Fragment(), CustomAdapterRecyclerView.ItemClickListener {
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

    }
}