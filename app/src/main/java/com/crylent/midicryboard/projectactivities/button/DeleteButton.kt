package com.crylent.midicryboard.projectactivities.button

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.app.AlertDialog
import com.crylent.midicryboard.projectactivities.Files
import com.crylent.midicryboard.R
import com.crylent.midicryboard.projectactivities.OpenProjectActivity
import java.io.IOException

class DeleteButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ProjectActionButton(context, attrs, R.drawable.delete) {

    override fun enable(projectName: String) {
        super.enable(projectName)
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
            Files.prj(context, name!!).delete()
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