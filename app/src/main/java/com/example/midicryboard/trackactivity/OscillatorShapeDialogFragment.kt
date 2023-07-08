package com.example.midicryboard.trackactivity

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.midicryboard.R
import com.example.midilib.Oscillator

class OscillatorShapeDialogFragment(
    private val selectedShape: Oscillator.Shape,
    private val callback: (Oscillator.Shape) -> Unit
): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
                .setView(R.layout.alertdialog_shapes)
            builder.create().apply {
                window!!.apply {
                    setBackgroundDrawableResource(R.color.transparent)
                    setWindowAnimations(R.style.ShapesDialogAnimation)
                }
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        (dialog as AlertDialog).apply {
            findViewById<RecyclerView>(R.id.shapes)!!.apply {
                adapter = ShapesRecyclerAdapter(selectedShape) {
                    callback(it)

                    // without this zero delay, dismission starts before callback I DON'T KNOW WHY
                    Handler(Looper.getMainLooper()).apply {
                        postDelayed({ dismiss() }, 0)
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "OscillatorShapeDialog"
    }
}