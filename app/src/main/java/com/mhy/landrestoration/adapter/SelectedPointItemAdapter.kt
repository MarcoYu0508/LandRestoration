package com.mhy.landrestoration.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mhy.landrestoration.databinding.PointSelectLayoutBinding
import com.mhy.landrestoration.model.SelectedPointItem

class SelectedPointItemAdapter(
    private val mapChoose: (Int) -> Unit,
    private val listChoose: (Int) -> Unit,
    private val onItemDelete: ((Int) -> Unit)?
) :
    ListAdapter<SelectedPointItem, SelectedPointItemAdapter.SelectedPointViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<SelectedPointItem>() {
            override fun areItemsTheSame(
                oldItem: SelectedPointItem,
                newItem: SelectedPointItem
            ): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: SelectedPointItem,
                newItem: SelectedPointItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedPointViewHolder {
        return SelectedPointViewHolder(
            PointSelectLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            mapChoose,
            listChoose,
            onItemDelete
        )
    }

    override fun onBindViewHolder(holder: SelectedPointViewHolder, position: Int) {
        holder.bind(position, getItem(position))
    }

    inner class SelectedPointViewHolder(
        private val binding: PointSelectLayoutBinding,
        private val mapChoose: (Int) -> Unit,
        private val listChoose: (Int) -> Unit,
        private val onItemDelete: ((Int) -> Unit)?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(index: Int, item: SelectedPointItem) {
            binding.apply {
                title.text = item.title
                item.name?.let {
                    etPointName.setText(it)
                }
                etPointName.addTextChangedListener {
                    item.name = it?.toString()
                }
                item.N?.let {
                    etN.setText(it.toString())
                }
                etN.addTextChangedListener {
                    item.N = it?.toString()?.toDouble()
                }
                item.E?.let {
                    etE.setText(it.toString())
                }
                etE.addTextChangedListener {
                    item.E = it?.toString()?.toDouble()
                }
                imgMapChoose.setOnClickListener {
                    mapChoose(index)
                }
                imgListChoose.setOnClickListener {
                    listChoose(index)
                }
                if (item.isDeletable) {
                    imgRemove.setOnClickListener {
                        onItemDelete?.invoke(index)
                    }
                } else {
                    imgRemove.visibility = View.GONE
                }
            }
        }
    }
}