package com.example.midicryboard.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midicryboard.*
import com.example.midicryboard.button.DeleteButton
import com.example.midicryboard.button.FavouriteButton
import com.example.midicryboard.button.OpenButton
import java.io.File

class OpenProjectActivity : AppCompatActivity() {
    private lateinit var nameFilter: EditText
    private lateinit var projectsRecyclerAdapter: ProjectsRecyclerAdapter
    private lateinit var favouriteButton: FavouriteButton
    private lateinit var deleteButton: DeleteButton
    private lateinit var openButton: OpenButton

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
        disableButtons()
    }

    private fun enableButtons() {
        favouriteButton.init(favourites, projectOnPreview!!)
        deleteButton.init(projectOnPreview!!)
        openButton.init(projectOnPreview!!)
    }

    private fun disableButtons() {
        favouriteButton.disable()
        deleteButton.disable()
        openButton.disable()
    }

    private var midiForPreview: File? = null

    fun tryToPreviewProject(projectName: String) {
        midiForPreview = File(filesDir, Filename.midi(projectName)).let { if (it.exists()) it else null }
        if (midiForPreview != null) {
            previewProject(projectName)
            return
        }
        // MIDI file is missing
        AlertDialog.Builder(this)
            .setTitle(R.string.error_no_midi)
            .setMessage(R.string.error_no_midi_msg)
            .setPositiveButton(R.string.yes) { _, _ ->
                previewProject(projectName)
            }
            .setNegativeButton(R.string.no) { _, _ -> }
            .show()
    }

    var projectOnPreview: String? = null
        private set

    private fun previewProject(projectName: String) {
        projectOnPreview = projectName
        enableButtons()
    }

    fun clearPreview() {
        projectOnPreview = null
        disableButtons()
    }

    fun removeProjectFromList(projectName: String) {
        projectsRecyclerAdapter.removeProjectFromList(projectName)
    }
}