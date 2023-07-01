package com.example.midicryboard.projectactivities.button

import android.content.Context
import android.util.AttributeSet
import com.example.midicryboard.Midi
import com.example.midicryboard.ProjectFile
import com.example.midicryboard.R
import com.example.midicryboard.projectactivities.Files
import com.example.midicryboard.projectactivities.ProjectExport

class AudioButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ProjectActionButton(context, attrs, R.drawable.audio) {

    override fun enable(projectName: String) {
        super.enable(projectName)
        setOnClickListener {
            shareWav()
        }
    }

    private fun shareWav() {
        if (name == null) return
        val currentProject = ProjectFile(context) // save current project
        ProjectFile.readFile(context, name!!).loadProject(context) // load project to be shared
        val wavFile = Files.wav(context, name!!).apply {
            writeBytes(Midi.renderWav())
            deleteOnExit()
        }
        ProjectExport.shareWav(context, wavFile)
        currentProject.loadProject(context) // load saved project
    }
}