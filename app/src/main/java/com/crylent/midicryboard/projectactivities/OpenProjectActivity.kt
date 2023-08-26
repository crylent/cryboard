package com.crylent.midicryboard.projectactivities

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crylent.midicryboard.R
import com.crylent.midicryboard.projectactivities.button.*

class OpenProjectActivity : AppCompatActivity() {
    private lateinit var nameFilter: EditText
    private lateinit var projectsRecyclerAdapter: ProjectsRecyclerAdapter
    private lateinit var favouriteButton: FavouriteButton
    private lateinit var deleteButton: DeleteButton
    private lateinit var openButton: OpenButton
    private lateinit var audioButton: AudioButton
    private lateinit var midiButton: MidiButton
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

        findViewById<ImageButton>(R.id.clearFilter).apply {
            setOnClickListener {
                nameFilter.setText("")
                projectsRecyclerAdapter.updateFilter("")
            }
        }
        findViewById<ImageButton>(R.id.onlyFavourites).apply {
            setOnClickListener {
                isActivated = !isActivated
                projectsRecyclerAdapter.showFavouritesOnly(isActivated)
            }
        }

        favouriteButton = findViewById(R.id.favouriteProject)
        deleteButton = findViewById(R.id.deleteProject)
        openButton = findViewById(R.id.openProject)
        audioButton = findViewById(R.id.shareWav)
        midiButton = findViewById(R.id.shareMidi)
        shareButton = findViewById(R.id.shareProject)
        disableButtons()
    }

    private fun enableButtons() {
        favouriteButton.enable(projectOnPreview!!, favourites)
        deleteButton.enable(projectOnPreview!!)
        openButton.enable(projectOnPreview!!)
        audioButton.enable(projectOnPreview!!)
        midiButton.enable(projectOnPreview!!)
        shareButton.enable(projectOnPreview!!)
    }

    private fun disableButtons() {
        favouriteButton.disable()
        deleteButton.disable()
        openButton.disable()
        audioButton.disable()
        midiButton.disable()
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