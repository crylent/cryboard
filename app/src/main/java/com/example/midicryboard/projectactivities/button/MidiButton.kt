package com.example.midicryboard.projectactivities.button

import android.content.Context
import android.util.AttributeSet
import com.example.midicryboard.*
import com.example.midicryboard.projectactivities.Files
import com.example.midicryboard.projectactivities.ProjectExport

class MidiButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ProjectActionButton(context, attrs, R.drawable.midi) {

    override fun enable(projectName: String) {
        super.enable(projectName)
        setOnClickListener {
            shareMidi()
        }
    }

    private fun shareMidi() {
        if (name == null) return
        val midiFile = Files.midi(context, name!!).apply {
            writeBytes(ProjectFile.readFile(context, this@MidiButton.name!!).midi)
        }
        ProjectExport.shareMidi(context, midiFile)
    }
}