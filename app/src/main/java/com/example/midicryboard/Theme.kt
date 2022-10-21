package com.example.midicryboard

import android.content.Context
import android.content.res.Configuration

enum class Theme {
    DARK, LIGHT;

    companion object {
        fun getTheme(context: Context): Theme {
            return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> LIGHT
                else -> DARK
            }
        }
    }
}