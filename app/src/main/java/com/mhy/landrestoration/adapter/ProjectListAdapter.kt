package com.mhy.landrestoration.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mhy.landrestoration.database.coordinate.Project
import com.mhy.landrestoration.databinding.ProjectItemBinding

class ProjectListAdapter(
    private val onItemInspect: (Project) -> Unit,
    private val onItemExport: (Project) -> Unit,
    private val onItemDelete: (Project) -> Unit
) :
    ListAdapter<Project, ProjectListAdapter.ProjectViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Project>() {
            override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
                return oldItem == newItem
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {

//        viewHolder.itemView.setOnClickListener {
//            val position = viewHolder.adapterPosition
//            onItemInspect(getItem(position))
//        }
        return ProjectViewHolder(
            ProjectItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemInspect,
            onItemExport,
            onItemDelete
        )
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int, ) {
        holder.bind(getItem(position))
    }

    class ProjectViewHolder(
        private var binding: ProjectItemBinding,
        private val onItemInspect: (Project) -> Unit,
        private val onItemExport: (Project) -> Unit,
        private val onItemDelete: (Project) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.txtTitle.text = project.name
            binding.btnShow.setOnClickListener {
                onItemInspect(project)
            }
            binding.btnExport.setOnClickListener {
                onItemExport(project)
            }
            binding.imageDelete.setOnClickListener {
                onItemDelete(project)
            }
        }
    }
}