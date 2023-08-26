package com.crylent.midicryboard.projectactivities

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object ProjectExport {
    private fun getUri(context: Context, file: File) = FileProvider.getUriForFile(
        context, "${context.packageName}.provider", file
    )

    private fun send(
        context: Context,
        file: File,
        mimeType: String,
        title: String
    ) = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra(Intent.EXTRA_STREAM, getUri(context, file))
        context.startActivity(Intent.createChooser(this, title))
    }

    fun shareProject(context: Context, project: File) {
        send(context, project, "application/octet-stream", "Export Project")
    }

    fun shareMidi(context: Context, midi: File) {
        send(context, midi, "audio/midi", "Export MIDI")
    }

    fun shareWav(context: Context, wav: File) {
        send(context, wav, "audio/wav", "Export WAV")
    }
}