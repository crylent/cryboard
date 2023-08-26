package com.crylent.midicryboard.projectactivities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crylent.midicryboard.R
import com.crylent.midicryboard.projectactivities.button.DeleteButton
import com.crylent.midicryboard.projectactivities.button.FavouriteButton
import java.io.File
import java.io.FilenameFilter

class ProjectsRecyclerAdapter(val context: OpenProjectActivity, private val favourites: Favourites):
    RecyclerView.Adapter<ProjectsRecyclerAdapter.ProjectViewHolder>() {
    class ProjectViewHolder(itemView: View, private val adapter: ProjectsRecyclerAdapter): RecyclerView.ViewHolder(itemView) {
        lateinit var projectName: String

        val item: LinearLayout = itemView.findViewById<LinearLayout?>(R.id.projectItem).apply {
            setOnClickListener {
                adapter.holders.forEach { holder ->
                    holder.item.isSelected = false
                }
                it.isSelected = true
                adapter.context.previewProject(name.text as String)
            }
        }

        val name: TextView = itemView.findViewById(R.id.projectFilename)

        val favourite: FavouriteButton = itemView.findViewById(R.id.favouriteButton)

        val delete: DeleteButton = itemView.findViewById(R.id.deleteButton)

        fun removeHolder() {
            adapter.apply {
                holders.remove(this@ProjectViewHolder)
                notifyItemRemoved(adapterPosition)
            }
        }
    }

    private var showFavouritesOnly = false

    private fun updateAndNotify(lambda: () -> Unit) {
        notifyItemRangeRemoved(0, itemCount)
        lambda()
        notifyItemRangeInserted(0, itemCount)
    }

    fun showFavouritesOnly(enable: Boolean = true) {
        updateAndNotify {
            showFavouritesOnly = enable
        }
    }

    fun updateFilter(filter: String) {
        updateAndNotify {
            ProjectFilter.filter = filter
        }
    }

    private object ProjectFilter: FilenameFilter {
        var filter = ""

        override fun accept(dir: File, name: String) = Files.projectFilter(name, filter)
    }

    private val holders = arrayListOf<ProjectViewHolder>()

    fun removeProjectFromList(projectName: String) {
        favourites.remove(projectName)
        holders.find { it.projectName == projectName }?.removeHolder()
    }

    private val dir = context.projectsDir
    private val files
        get() = (dir.listFiles(ProjectFilter) ?: emptyArray()).filter {
            if (showFavouritesOnly) favourites.contains(it.nameWithoutExtension)
            else true
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProjectViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.project, parent, false),
        this
    ).apply {
        holders.add(this)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.apply {
            projectName = files[position].nameWithoutExtension
            name.text = projectName
            favourite.enable(projectName, favourites)
            delete.enable(projectName)
        }
    }

    override fun getItemCount() = files.size
}