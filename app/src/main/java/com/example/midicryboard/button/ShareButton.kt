package com.example.midicryboard.button

import android.content.Context
import android.util.AttributeSet

class ShareButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ProjectActionButton(context, attrs) {

    fun init(projectName: String) {
        isEnabled = true
        name = projectName
        setOnClickListener {
            //ProjectExport.shareMidi(context)
        }
    }
}