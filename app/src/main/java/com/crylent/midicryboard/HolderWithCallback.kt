package com.crylent.midicryboard

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class HolderWithCallback(itemView: View) : RecyclerView.ViewHolder(itemView) {
    protected fun interface OnBound {
        fun onBound()
    }

    private val onBoundCallbacks = mutableListOf<OnBound>()

    protected fun onBound(callback: OnBound) {
        onBoundCallbacks.add(callback)
    }

    protected fun invokeCallback() {
        onBoundCallbacks.forEach {
            it.onBound()
        }
    }
}