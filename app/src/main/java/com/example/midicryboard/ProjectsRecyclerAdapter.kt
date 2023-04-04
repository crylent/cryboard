package com.example.midicryboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midicryboard.activity.OpenProjectActivity
import java.io.File
import java.io.FilenameFilter

class ProjectsRecyclerAdapter(val context: OpenProjectActivity): RecyclerView.Adapter<ProjectsRecyclerAdapter.ProjectViewHolder>() {
    class ProjectViewHolder(itemView: View, adapter: ProjectsRecyclerAdapter): RecyclerView.ViewHolder(itemView) {
        //lateinit var file: File

        val item: LinearLayout = itemView.findViewById<LinearLayout?>(R.id.projectItem).apply {
            setOnClickListener {
                adapter.holders.forEach { holder ->
                    holder.item.isSelected = false
                }
                it.isSelected = true
                adapter.context.tryToPreviewProject(name.text as String)
            }
        }

        val name: TextView = itemView.findViewById(R.id.projectFilename)

        val favourite: ImageButton = itemView.findViewById<ImageButton?>(R.id.favouriteButton).apply {
            setOnClickListener {
                isActivated = !isActivated
                if (isActivated)
                    adapter.favourites.add(name.text.toString())
                else
                    adapter.favourites.remove(name.text.toString())
                adapter.prefs.edit().apply {
                    putStringSet(FAVOURITES, adapter.favourites)
                    apply()
                }
            }
        }

        val delete: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    fun updateFilter(filter: String) {
        ProjectFilter.filter = filter
    }

    private object ProjectFilter: FilenameFilter {
        var filter = ""

        override fun accept(dir: File, name: String) = Filename.projectFilter(name, filter)
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val favourites = prefs.getStringSet(FAVOURITES, emptySet())!!.toMutableSet()

    private val holders = arrayListOf<ProjectViewHolder>()

    private val dir = context.filesDir
    private val files
        get() = dir.listFiles(ProjectFilter)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProjectViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.project, parent, false),
        this
    ).apply {
        holders.add(this)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.apply {
            //file = files!![position]
            name.text = files!![position].nameWithoutExtension
            favourite.isActivated = favourites.contains(name.text)
        }
    }

    override fun getItemCount() = files?.size ?: 0

    companion object {
        private const val FAVOURITES = "favourites"
    }
}