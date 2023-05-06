package com.example.midicryboard.button

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import com.example.midicryboard.*
import org.json.JSONObject
import java.io.File
import java.io.InputStreamReader

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
        context.openFileInput(Filename.metadata(name!!)).use { file ->
            InputStreamReader(file).use {
                val project = CryboardProject.fromJson(context, JSONObject(it.readText()))
                TrackList.readTracksFromProject(project)
            }
        }
        Midi.readFromFile(File(context.filesDir, Filename.midi(name!!)))
    }
}