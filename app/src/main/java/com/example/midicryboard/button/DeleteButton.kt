package com.example.midicryboard.button

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.app.AlertDialog
import com.example.midicryboard.Filename
import com.example.midicryboard.R
import com.example.midicryboard.activity.OpenProjectActivity
import java.io.File
import java.io.IOException

class DeleteButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ProjectActionButton(context, attrs) {

    fun init(projectName: String) {
        isEnabled = true
        name = projectName
        setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("${context.getString(R.string.delete_project)} $projectName?")
                .setMessage(R.string.delete_project_msg)
                .setPositiveButton(R.string.yes) { _, _ ->
                    deleteProject()
                }
                .setNegativeButton(R.string.no) { _, _ -> }
                .show()
        }
    }

    private fun deleteProject() {
        if (name == null) return
        try {
            File(context.filesDir, Filename.metadata(name!!)).delete()
            File(context.filesDir, Filename.midi(name!!)).apply {
                if (exists()) delete()
            }
            (context as OpenProjectActivity).apply {
                removeProjectFromList(name!!)
                if (projectOnPreview == name) clearPreview()
            }
        }
        catch (e: IOException) {
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.delete_project_error))
                .setMessage(e.message)
                .setNeutralButton(R.string.ok) { _, _ -> }
                .show()
        }
    }
}