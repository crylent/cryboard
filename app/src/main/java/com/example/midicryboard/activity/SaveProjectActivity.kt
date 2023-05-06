package com.example.midicryboard.activity

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.midicryboard.CryboardProject
import com.example.midicryboard.Filename
import com.example.midicryboard.Midi
import com.example.midicryboard.R
import java.io.File
import java.io.OutputStreamWriter

class SaveProjectActivity : AppCompatActivity() {
    private lateinit var projectNameEdit: EditText
    private val projectName
        get() = projectNameEdit.text.toString()

    private lateinit var saveButton: AppCompatButton
    private lateinit var exportButton: AppCompatButton

    private lateinit var errorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_project)
        projectNameEdit = findViewById<EditText?>(R.id.projectName).apply {
            addTextChangedListener {
                allowToProceed(Filename.check(it.toString()))
            }
        }
        saveButton = findViewById<AppCompatButton?>(R.id.saveProject).apply {
            setOnClickListener {
                saveFile(projectName)
                finish()
            }
        }
        exportButton = findViewById<AppCompatButton?>(R.id.exportMidi).apply {
            //Intent(Intent.ACTION_SEND)
        }
        errorText = findViewById(R.id.saveProjectError)
    }

    private fun saveFile(projectName: String) {
        openFileOutput(Filename.metadata(projectName), Context.MODE_PRIVATE).use { file ->
            OutputStreamWriter(file).use {
                it.write(CryboardProject().toJson().toString())
            }
        }
        Midi.writeToFile(File(filesDir, Filename.midi(projectName)))
    }

    private fun allowToProceed(allow: Boolean) {
        saveButton.isEnabled = allow
        exportButton.isEnabled = allow
        errorText.apply {
            if (!allow) setText(R.string.error_invalid_project_name)
            isVisible = !allow
        }
    }
}