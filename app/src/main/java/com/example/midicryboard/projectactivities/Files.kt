package com.example.midicryboard.projectactivities

import android.content.Context
import java.io.File

object Files {
    private const val PROJECT_EXTENSION = ".prj"
    private const val MIDI_EXTENSION = ".mid"
    private const val WAV_EXTENSION = ".wav"

    private const val PRESET_EXTENSION = ".json"

    private const val FIRST_CHAR = "\\p{Alnum}"
    private const val CHARACTERS = "\\p{Alnum} _-"

    fun prj(context: Context, projectName: String, temp: Boolean = false) = File(
        if (temp) context.cacheDir else context.projectsDir,
        "$projectName$PROJECT_EXTENSION"
    )
    fun midi(context: Context, projectName: String) = File(context.cacheDir, "$projectName$MIDI_EXTENSION")
    fun wav(context: Context, projectName: String) = File(context.cacheDir, "$projectName$WAV_EXTENSION")

    fun preset(context: Context, categoryName: String, instrumentName: String) =
        File(presetsDir(context, categoryName), "$instrumentName$PRESET_EXTENSION")

    fun asset(context: Context) = File(
        context.importedAssetsDir,
        "${System.currentTimeMillis().hashCode()}$WAV_EXTENSION"
    )

    fun nameCheck(projectName: String) =
        projectName.matches(Regex("$FIRST_CHAR[$CHARACTERS]*"))

    fun projectFilter(name: String, filter: String) =
        name.lowercase().matches(Regex("[$CHARACTERS]*$filter[$CHARACTERS]*$PROJECT_EXTENSION"))

    fun presetsDir(context: Context, categoryName: String) =
        File(context.presetsDir, "$categoryName/").checkExistence()
}

private fun File.checkExistence(): File {
    if (!exists()) mkdir()
    return this
}

val Context.presetsDir get() = File(filesDir, "presets/").checkExistence()
val Context.importedAssetsDir get() = File(filesDir, "assets/").checkExistence()
val Context.projectsDir get() = File(filesDir, "projects/").checkExistence()