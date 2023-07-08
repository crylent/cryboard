package com.example.midicryboard.trackactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.midicryboard.HolderWithCallback
import com.example.midicryboard.R
import com.example.midilib.Oscillator

class ShapesRecyclerAdapter(
    private val selectedShape: Oscillator.Shape,
    private val callback: (Oscillator.Shape) -> Unit
): RecyclerView.Adapter<ShapesRecyclerAdapter.ShapeViewHolder>() {

    class ShapeViewHolder(itemView: View, adapter: ShapesRecyclerAdapter): HolderWithCallback(itemView) {
        var shape: Oscillator.Shape? = null
            set(value) {
                field = value
                invokeCallback()
            }

        private val button = (itemView as ImageButton).apply {
            onBound {
                if (shape == adapter.selectedShape) {
                    isSelected = true
                }
            }
        }

        init {
            button.setOnClickListener {
                adapter.apply {
                    holders.forEach { holder: ShapeViewHolder ->
                        holder.button.isSelected = (holder == this@ShapeViewHolder)
                    }
                    callback(shape!!)
                }
            }
            onBound {
                button.setImageResource(shapes[shape]!!)
            }
        }
    }

    private val holders = arrayListOf<ShapeViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ShapeViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.shape, parent, false),
        this
    ).apply {
        holders.add(this)
    }


    override fun onBindViewHolder(holder: ShapeViewHolder, position: Int) {
        holder.apply {
            shape = Oscillator.Shape.values()[position]
        }
    }

    override fun getItemCount() = shapes.size

    companion object {
        val shapes = mapOf(
            Oscillator.Shape.SINE to R.drawable.sine,
            Oscillator.Shape.TRIANGLE to R.drawable.triangle,
            Oscillator.Shape.SQUARE to R.drawable.square,
            Oscillator.Shape.SAW to R.drawable.saw,
            Oscillator.Shape.REVERSE_SAW to R.drawable.reverse_saw
        )
    }
}
