package com.crylent.midicryboard.projectactivities.button

import android.content.Context
import android.util.AttributeSet
import com.crylent.midicryboard.Midi
import com.crylent.midicryboard.ProjectFile
import com.crylent.midicryboard.R
import com.crylent.midicryboard.projectactivities.Files
import com.crylent.midicryboard.projectactivities.ProjectExport

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