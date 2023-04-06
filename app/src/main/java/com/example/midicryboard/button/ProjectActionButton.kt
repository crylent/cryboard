package com.example.midicryboard.button

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

abstract class ProjectActionButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageButton(context, attrs) {
    internal var name: String? = null

    fun disable() {
        isEnabled = false
        name = null
    }
}