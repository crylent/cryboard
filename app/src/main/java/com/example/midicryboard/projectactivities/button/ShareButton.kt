package com.example.midicryboard.projectactivities.button

import android.content.Context
import android.util.AttributeSet
import com.example.midicryboard.R
import com.example.midicryboard.projectactivities.Files
import com.example.midicryboard.projectactivities.ProjectExport

class ShareButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ProjectActionButton(context, attrs, R.drawable.share) {

    override fun enable(projectName: String) {
        super.enable(projectName)
        setOnClickListener {
            shareProject()
        }
    }

    private fun shareProject() {
        if (name == null) return
        ProjectExport.shareProject(context, Files.prj(context, name!!))
    }
}