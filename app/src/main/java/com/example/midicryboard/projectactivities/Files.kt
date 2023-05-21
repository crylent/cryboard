package com.example.midicryboard.projectactivities

import android.content.Context
import java.io.File

object Files {
    private const val PROJECT_EXTENSION = ".prj"
    private const val MIDI_EXTENSION = ".mid"
    private const val WAV_EXTENSION = ".wav"

    private const val FIRST_CHAR = "\\p{Alnum}"
    private const val CHARACTERS = "\\p{Alnum} _-"

    fun prj(context: Context, projectName: String, temp: Boolean = false) = File(
        if (temp) context.tempDir else context.projectsDir,
        "$projectName$PROJECT_EXTENSION"
    )
    fun midi(context: Context, projectName: String) = File(context.tempDir, "$projectName$MIDI_EXTENSION")
    fun wav(context: Context, projectName: String) = File(context.tempDir, "$projectName$WAV_EXTENSION")

    fun nameCheck(projectName: String) =
        projectName.matches(Regex("$FIRST_CHAR[$CHARACTERS]*"))

    fun projectFilter(name: String, filter: String) =
        name.lowercase().matches(Regex("[$CHARACTERS]*$filter[$CHARACTERS]*$PROJECT_EXTENSION"))
}

private fun File.checkExistence(): File {
    if (!exists()) mkdir()
    return this
}

val Context.projectsDir get() = File(filesDir, "projects/").checkExistence()
val Context.tempDir get() = File(filesDir, "temp/").checkExistence()