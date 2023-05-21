package com.example.midicryboard.projectactivities

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midicryboard.Favourites
import com.example.midicryboard.R
import com.example.midicryboard.button.DeleteButton
import com.example.midicryboard.button.FavouriteButton
import com.example.midicryboard.button.OpenButton
import com.example.midicryboard.button.ShareButton

class OpenProjectActivity : AppCompatActivity() {
    private lateinit var nameFilter: EditText
    private lateinit var projectsRecyclerAdapter: ProjectsRecyclerAdapter
    private lateinit var favouriteButton: FavouriteButton
    private lateinit var deleteButton: DeleteButton
    private lateinit var openButton: OpenButton
    private lateinit var shareButton: ShareButton

    private lateinit var favourites: Favourites

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_project)
        favourites = Favourites.getInstance(this)
        projectsRecyclerAdapter = ProjectsRecyclerAdapter(this@OpenProjectActivity, favourites)
        findViewById<RecyclerView>(R.id.projects).apply {
            adapter = projectsRecyclerAdapter
            layoutManager = LinearLayoutManager(this@OpenProjectActivity)
        }
        nameFilter = findViewById<EditText?>(R.id.projectNameFilter).apply {
            addTextChangedListener {
                projectsRecyclerAdapter.updateFilter(it.toString())
            }
        }
        favouriteButton = findViewById(R.id.favouriteProject)
        deleteButton = findViewById(R.id.deleteProject)
        openButton = findViewById(R.id.openProject)
        shareButton = findViewById(R.id.shareProject)
        disableButtons()
    }

    private fun enableButtons() {
        favouriteButton.init(projectOnPreview!!, favourites)
        deleteButton.init(projectOnPreview!!)
        openButton.init(projectOnPreview!!)
        shareButton.init(projectOnPreview!!)
    }

    private fun disableButtons() {
        favouriteButton.disable()
        deleteButton.disable()
        openButton.disable()
        shareButton.disable()
    }

    fun previewProject(projectName: String) {
        projectOnPreview = projectName
        enableButtons()
    }

    var projectOnPreview: String? = null
        private set

    fun clearPreview() {
        projectOnPreview = null
        disableButtons()
    }

    fun removeProjectFromList(projectName: String) {
        projectsRecyclerAdapter.removeProjectFromList(projectName)
    }
}