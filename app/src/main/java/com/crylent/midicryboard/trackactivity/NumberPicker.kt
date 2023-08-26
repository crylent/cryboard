package com.crylent.midicryboard.trackactivity

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IdRes
import com.crylent.midicryboard.R

class NumberPicker @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
): LinearLayout(context, attrs) {

    private lateinit var upButton: ImageButton
    private lateinit var downButton: ImageButton
    private lateinit var pickedNumber: TextView
    private lateinit var pickerLabel: TextView

    private val attributes = context.obtainStyledAttributes(attrs, R.styleable.NumberPicker, 0, 0)

    var value = 0
        set(value) {
            field = value
            updateState()
        }

    private var min = Int.MIN_VALUE
    private var max = Int.MAX_VALUE

    private var wasAttachedAlready = false

    private fun setupButton(@IdRes id: Int, delta: Int) = findViewById<ImageButton>(id).apply {
        setOnClickListener {
            value += delta
            onValueChanged(delta)
        }
    }

    override fun onAttachedToWindow() {
        if (wasAttachedAlready) return

        inflate(context, R.layout.number_picker, this)

        upButton = setupButton(R.id.upButton, 1)
        downButton = setupButton(R.id.downButton, -1)
        pickedNumber = findViewById(R.id.pickedNumber)
        pickerLabel = findViewById(R.id.pickerLabel)

        pickerLabel.text = attributes.getText(R.styleable.NumberPicker_labelText)
        min = attributes.getInt(R.styleable.NumberPicker_min, min)
        max = attributes.getInt(R.styleable.NumberPicker_max, max)
        if (value == 0) value = attributes.getInt(R.styleable.NumberPicker_value, value)
        attributes.recycle()

        super.onAttachedToWindow()
        wasAttachedAlready = true

        updateState()
    }

    fun interface OnValueChangedListener {
        fun onValueChanged(value: Int, delta: Int)
    }

    private var onValueChangedListener: OnValueChangedListener? = null

    fun setOnValueChangedListener(listener: OnValueChangedListener) {
        onValueChangedListener = listener
    }

    private fun onValueChanged(delta: Int) {
        updateState()
        onValueChangedListener?.onValueChanged(value, delta)
    }

    private fun updateState() {
        if (wasAttachedAlready) {
            pickedNumber.text = value.toString()
            upButton.isEnabled = value < max
            downButton.isEnabled = value > min
        }
    }
}