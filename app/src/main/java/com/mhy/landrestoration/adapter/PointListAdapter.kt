package com.mhy.landrestoration.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mhy.landrestoration.database.model.Coordinate
import com.mhy.landrestoration.databinding.PointItemBinding

class PointListAdapter(
    private val onItemEdit: (Coordinate) -> Unit,
    private val onItemDelete: (Coordinate) -> Unit
) : ListAdapter<Coordinate, PointListAdapter.PointViewHolder>(DiffCallback) {


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Coordinate>() {
            override fun areItemsTheSame(oldItem: Coordinate, newItem: Coordinate): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Coordinate, newItem: Coordinate): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        return PointViewHolder(
            PointItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemEdit,
            onItemDelete
        )
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PointViewHolder(
        private var binding: PointItemBinding,
        private val onItemEdit: (Coordinate) -> Unit,
        private val onItemDelete: (Coordinate) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coordinate: Coordinate) {
            binding.txtName.text = coordinate.name
            binding.txtN.text = coordinate.N.toString()
            binding.txtE.text = coordinate.E.toString()

            binding.btnEdit.setOnClickListener {
                onItemEdit(coordinate)
            }

            binding.imgDelete.setOnClickListener {
                onItemDelete(coordinate)
            }
        }
    }
}