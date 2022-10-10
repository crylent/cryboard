package com.example.midicryboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import com.example.midicryboard.activity.*

class TrackPropertiesActivityContract: ActivityResultContract<Bundle, Bundle?>() {
    override fun createIntent(context: Context, input: Bundle) =
        Intent(context, TrackPropertiesActivity::class.java)
            .putExtra(TrackBundle.INPUT, input)

    override fun parseResult(resultCode: Int, intent: Intent?): Bundle? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> intent?.getBundleExtra(TrackBundle.OUTPUT)
    }
}