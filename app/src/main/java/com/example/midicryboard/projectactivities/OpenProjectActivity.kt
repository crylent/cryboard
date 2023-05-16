package com.example.midicryboard.projectactivities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midicryboard.Favourites
import com.example.midicryboard.Filename
import com.example.midicryboard.ProjectsRecyclerAdapter
import com.example.midicryboard.R
import com.example.midicryboard.button.DeleteButton
import com.example.midicryboard.button.FavouriteButton
import com.example.midicryboard.button.OpenButton
import com.example.midicryboard.button.ShareButton
import java.io.File

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

    fun shareMidi() {
        Intent(Intent.ACTION_SEND).apply {
            type = "audio/midi"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                this@OpenProjectActivity,
                "$packageName.provider",
                midiForPreview!!
            ))
            startActivity(Intent.createChooser(this, "Share MIDI"))
        }
    }
}