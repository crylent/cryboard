package com.crylent.midicryboard.projectactivities.button

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

abstract class ProjectActionButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, private val defaultImageRes: Int? = null
) : AppCompatImageButton(context, attrs) {
    override fun onAttachedToWindow() {
        layoutParams = layoutParams.apply {
            if (drawable == null && defaultImageRes != null) setImageResource(defaultImageRes)
        }
        super.onAttachedToWindow()
    }

    protected var name: String? = null

    open fun enable(projectName: String) {
        isEnabled = true
        name = projectName
    }

    fun disable() {
        isEnabled = false
        name = null
    }
}