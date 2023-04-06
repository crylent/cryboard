package com.example.midicryboard.button

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import com.example.midicryboard.CryboardProject
import com.example.midicryboard.Filename
import com.example.midicryboard.Midi
import com.example.midicryboard.TrackList
import java.io.File
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
        context.openFileInput(Filename.metadata(name!!)).use { file ->
            ObjectInputStream(file).use {
                TrackList.openProject(it.readObject() as CryboardProject)
            }
        }
        Midi.readFromFile(File(context.filesDir, Filename.midi(name!!)))
    }
}