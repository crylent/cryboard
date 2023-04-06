package com.example.midicryboard.button

import android.content.Context
import android.util.AttributeSet
import com.example.midicryboard.activity.OpenProjectActivity

class ShareButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ProjectActionButton(context, attrs) {

    fun init(projectName: String) {
        isEnabled = true
        name = projectName
        setOnClickListener {
            (context as OpenProjectActivity).shareMidi()
        }
    }
}