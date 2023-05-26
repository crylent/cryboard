package com.example.midicryboard

import android.content.Context
import com.example.midicryboard.projectactivities.Files
import org.json.JSONObject
import java.io.*

data class ProjectFile(val metadata: String, val midi: ByteArray): Serializable {
    //constructor(metadata: String, midi: File): this(metadata, midi.readBytes())
    constructor() : this(
        ProjectMetadata().toJson().toString(),
        Midi.writeToFile(java.nio.file.Files.createTempFile("midi", ".mid").toFile()).run {
            readBytes().also {
                delete()
            }
        } // create temporary MIDI file, read bytes from it and delete it
    )

    fun save(context: Context, name: String, temp: Boolean = false): File {
        return Files.prj(context, name, temp).apply {
            FileOutputStream(this).use { file ->
                ObjectOutputStream(file).use {
                    it.writeObject(this)
                }
            }
        }
    }

    fun loadProject(context: Context) {
        TrackList.readTracksFromProject(
            ProjectMetadata.fromJson(context, JSONObject(metadata))
        )
        Midi.readFromBytes(midi)
    }

    companion object {
        private const val serialVersionUID = -90000100L

        fun saveCurrentProject(context: Context, name: String, temp: Boolean = false): File {
            return ProjectFile().save(context, name, temp)
        }

        fun readFromStream(stream: InputStream): ProjectFile {
            stream.use { file ->
                ObjectInputStream(file).use {
                    return it.readObject() as ProjectFile
                }
            }
        }

        fun readFile(context: Context, name: String): ProjectFile {
            return readFromStream(FileInputStream(Files.prj(context, name)))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProjectFile) return false

        if (metadata != other.metadata) return false
        if (!midi.contentEquals(other.midi)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = metadata.hashCode()
        result = 31 * result + midi.contentHashCode()
        return result
    }
}