package com.mhy.landrestoration.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mhy.landrestoration.database.model.CalculateResult
import com.mhy.landrestoration.databinding.CalculateResultItemBinding

class CalculateResultListAdapter(
    private val onItemInspect: (CalculateResult) -> Unit,
    private val onItemExport: (CalculateResult) -> Unit,
    private val onItemDelete: (CalculateResult) -> Unit,
    private val isExport: Boolean = false
) : ListAdapter<CalculateResult, CalculateResultListAdapter.CalculateResultViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CalculateResult>() {
            override fun areItemsTheSame(
                oldItem: CalculateResult,
                newItem: CalculateResult
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CalculateResult,
                newItem: CalculateResult
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculateResultViewHolder {
        val binding = CalculateResultItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalculateResultViewHolder(
            binding,
            onItemInspect,
            onItemExport,
            onItemDelete,
            isExport
        )
    }

    override fun onBindViewHolder(holder: CalculateResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CalculateResultViewHolder(
        private val binding: CalculateResultItemBinding,
        private val onItemInspect: (CalculateResult) -> Unit,
        private val onItemExport: (CalculateResult) -> Unit,
        private val onItemDelete: (CalculateResult) -> Unit,
        private val isExport: Boolean
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(calculateResult: CalculateResult) {
            binding.apply {
                tvName.text = calculateResult.name
                btnInspect.setOnClickListener {
                    onItemInspect(calculateResult)
                }
                if (isExport) {
                    btnExport.visibility = View.VISIBLE
                    btnExport.setOnClickListener {
                        onItemExport(calculateResult)
                    }
                } else {
                    btnExport.visibility = View.GONE
                }

                imgDelete.setOnClickListener {
                    onItemDelete(calculateResult)
                }
            }
        }
    }
}