package com.crylent.midicryboard.projectactivities.button

import android.content.Context
import android.util.AttributeSet
import com.crylent.midicryboard.projectactivities.Favourites
import com.crylent.midicryboard.R

class FavouriteButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ProjectActionButton(context, attrs, R.drawable.favourite_mark) {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        buttons.add(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        buttons.remove(this)
    }

    fun enable(projectName: String, favourites: Favourites) {
        isActivated = favourites.contains(projectName)
        super.enable(projectName)
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