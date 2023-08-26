package com.crylent.midicryboard.projectactivities.button

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import com.crylent.midicryboard.ProjectFile
import com.crylent.midicryboard.R
import com.crylent.midicryboard.mainactivity.MainActivity

class OpenButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ProjectActionButton(context, attrs, R.drawable.open) {

    override fun enable(projectName: String) {
        super.enable(projectName)
        setOnClickListener {
            openFile()
            (context as Activity).finish()
        }
    }

    private fun openFile() {
        if (name == null) return
        ProjectFile.readFile(context, name!!).loadProject(context)
        MainActivity.projectUnsaved = false
    }
}