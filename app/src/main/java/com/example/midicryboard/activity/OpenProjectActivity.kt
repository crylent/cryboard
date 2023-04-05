package com.example.midicryboard.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midicryboard.*
import java.io.File
import java.io.ObjectInputStream

class OpenProjectActivity : AppCompatActivity() {
    private lateinit var nameFilter: EditText
    private lateinit var projectsRecyclerAdapter: ProjectsRecyclerAdapter
    private lateinit var favouriteButton: FavouriteButton
    private lateinit var deleteButton: ImageButton
    private lateinit var openButton: ImageButton

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
        setButtonsEnabled(false)
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        favouriteButton.isEnabled = enabled
        deleteButton.isEnabled = enabled
        openButton.isEnabled = enabled
    }

    private var midiForPreview: File? = null

    fun tryToPreviewProject(projectName: String) {
        midiForPreview = File(filesDir, Filename.midi(projectName)).let { if (it.exists()) it else null }
        if (midiForPreview != null) {
            previewProject(projectName)
            return
        }
        // MIDI file not found
        var denyDismissListener = false
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error_no_midi))
            .setMessage(getString(R.string.error_no_midi_desc))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                denyDismissListener = true
                previewProject(projectName)
            }
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setOnDismissListener {
                if (denyDismissListener) return@setOnDismissListener
                setButtonsEnabled(false) // disable buttons
            }
            .show()
    }

    private fun previewProject(projectName: String) {
        favouriteButton.apply {
            init(favourites, projectName)
        }
        setButtonsEnabled(true) // enable buttons
        openButton.setOnClickListener {
            openFile(projectName)
            finish()
        }
    }

    private fun openFile(projectName: String) {
        openFileInput(Filename.metadata(projectName)).use { file ->
            ObjectInputStream(file).use {
                TrackList.openProject(it.readObject() as CryboardProject)
            }
        }
        Midi.readFromFile(File(filesDir, Filename.midi(projectName)))
    }
}