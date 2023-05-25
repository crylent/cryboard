package com.example.midicryboard.projectactivities

import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import com.example.midicryboard.Midi
import com.example.midicryboard.ProjectFile
import com.example.midicryboard.R
import java.io.File

class SaveProjectActivity : AppCompatActivity(), OnItemSelectedListener {
    private lateinit var projectNameEdit: EditText
    private val projectName
        get() = projectNameEdit.text.toString()

    private lateinit var formatSpinner: Spinner
    private lateinit var saveButton: ImageButton
    private lateinit var exportButton: ImageButton

    private lateinit var errorText: TextView

    private var canSave = true
    private var correctFilename = false

    private enum class ExportFormat(val description: String) {
        PROJECT("Project (.prj)"),
        MIDI("MIDI (.mid)"),
        WAV("Audio (.wav)");

        companion object {
            val descriptions = values().map { it.description }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_project)
        projectNameEdit = findViewById<EditText>(R.id.projectName).apply {
            addTextChangedListener {
                correctFilename = Files.nameCheck(it.toString())
                errorText.apply {
                    if (!correctFilename) setText(R.string.error_invalid_project_name)
                    isGone = correctFilename
                }
                updateButtons()
            }
        }
        formatSpinner = findViewById<Spinner>(R.id.saveProjectFormat).apply {
            adapter = ArrayAdapter<String>(
                this@SaveProjectActivity, android.R.layout.simple_spinner_item
            ).apply {
                addAll(ExportFormat.descriptions)
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            onItemSelectedListener = this@SaveProjectActivity
        }
        saveButton = findViewById<ImageButton>(R.id.saveProject).apply {
            setOnClickListener {
                saveProject(projectName)
                finish()
            }
        }
        exportButton = findViewById<ImageButton>(R.id.exportProject).apply {
            setOnClickListener {
                when (ExportFormat.values()[formatSpinner.selectedItemPosition]) {
                    ExportFormat.PROJECT -> {
                        val projectFile = saveProject(projectName, true)
                        ProjectExport.shareProject(this@SaveProjectActivity, projectFile)
                    }
                    ExportFormat.MIDI -> {
                        val midiFile = Midi.writeToFile(this@SaveProjectActivity, projectName).apply {
                            deleteOnExit()
                        }
                        ProjectExport.shareMidi(this@SaveProjectActivity, midiFile)
                    }
                    ExportFormat.WAV -> {
                        val wavFile = Files.wav(this@SaveProjectActivity, projectName).apply {
                            writeBytes(Midi.renderWav())
                            deleteOnExit()
                        }
                        ProjectExport.shareWav(this@SaveProjectActivity, wavFile)
                    }
                }
                finish()
            }
        }
        errorText = findViewById(R.id.saveProjectError)
    }

    private fun saveProject(projectName: String, temp: Boolean = false): File {
        return ProjectFile.saveCurrentProject(this, projectName, temp)
    }

    private fun updateButtons() {
        saveButton.isEnabled = correctFilename && canSave
        exportButton.isEnabled = correctFilename
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        canSave = when (ExportFormat.values()[position]) {
            ExportFormat.PROJECT -> true
            ExportFormat.MIDI -> false
            ExportFormat.WAV -> false
        }
        updateButtons()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}