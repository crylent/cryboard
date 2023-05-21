package com.example.midicryboard.button

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import com.example.midicryboard.*
import com.example.midicryboard.projectactivities.Files
import org.json.JSONObject
import java.io.FileInputStream
import java.io.ObjectInputStream

class OpenButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ProjectActionButton(context, attrs) {

    fun init(projectName: String) {
        isEnabled = true
        name = projectName
        setOnClickListener {
            openFile()
            (context as Activity).finish()
        }
    }

    private fun openFile() {
        if (name == null) return
        FileInputStream(Files.prj(context, name!!)).use { file ->
            ObjectInputStream(file).use {
                val project = it.readObject() as ProjectFile
                val metadata = ProjectMetadata.fromJson(context, JSONObject(project.metadata))
                TrackList.readTracksFromProject(metadata)
                Midi.readFromBytes(project.midi)
            }
        }
    }
}