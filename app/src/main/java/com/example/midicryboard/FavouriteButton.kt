package com.example.midicryboard

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

class FavouriteButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageButton(context, attrs) {
    lateinit var name: String

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        buttons.add(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        buttons.remove(this)
    }

    fun init(favourites: Favourites, projectName: String) {
        isActivated = favourites.contains(projectName)
        name = projectName
        setOnClickListener {
            buttons.forEach {
                if (it::name.isInitialized && name == it.name) it.isActivated = !it.isActivated
            }
            if (isActivated)
                favourites.add(name)
            else
                favourites.remove(name)
        }
    }

    companion object {
        private val buttons = mutableSetOf<FavouriteButton>()
    }
}