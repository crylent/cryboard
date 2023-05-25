package com.example.midicryboard.projectactivities

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class Favourites private constructor(set: Set<String>, private val prefs: SharedPreferences): LinkedHashSet<String>(set) {

    var initialized = false

    init {
        initialized = true
    }

    override fun add(element: String): Boolean {
        return super.add(element).also {
            if (initialized) updatePrefs()
        }
    }

    override fun remove(element: String): Boolean {
        return super.remove(element).also {
            updatePrefs()
        }
    }

    private fun updatePrefs() {
        prefs.edit().apply {
            putStringSet(PREFS_NAME, this@Favourites)
            apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "favourites"

        fun getInstance(context: Context): Favourites {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return Favourites(prefs.getStringSet(PREFS_NAME, emptySet())!!, prefs)
        }
    }
}