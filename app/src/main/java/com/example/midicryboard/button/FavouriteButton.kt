package com.example.midicryboard.button

import android.content.Context
import android.util.AttributeSet
import com.example.midicryboard.Favourites

class FavouriteButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ProjectActionButton(context, attrs) {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        buttons.add(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        buttons.remove(this)
    }

    fun init(favourites: Favourites, projectName: String) {
        isEnabled = true
        isActivated = favourites.contains(projectName)
        name = projectName
        setOnClickListener {
            if (name == null) return@setOnClickListener
            buttons.forEach {
                if (name == it.name) it.isActivated = !it.isActivated
            }
            if (isActivated)
                favourites.add(name!!)
            else
                favourites.remove(name)
        }
    }

    companion object {
        private val buttons = mutableSetOf<FavouriteButton>()
    }
}