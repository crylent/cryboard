package com.example.midicryboard

object Filename {
    private const val METADATA_EXTENSION = ".tracks"
    private const val MIDI_EXTENSION = ".mid"

    private const val FIRST_CHAR = "\\p{Alnum}"
    private const val CHARACTERS = "\\p{Alnum} _-"

    fun metadata(projectName: String) = "$projectName$METADATA_EXTENSION"
    fun midi(projectName: String) = "$projectName$MIDI_EXTENSION"

    fun check(projectName: String) =
        projectName.matches(Regex("$FIRST_CHAR[$CHARACTERS]*"))

    fun projectFilter(name: String, filter: String) =
        name.lowercase().matches(Regex("[$CHARACTERS]*$filter[$CHARACTERS]*$METADATA_EXTENSION$"))
}