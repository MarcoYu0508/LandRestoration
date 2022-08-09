package com.mhy.landrestoration.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mhy.landrestoration.database.model.CalculateResult
import com.mhy.landrestoration.databinding.CalculateResultBinding
import com.mhy.landrestoration.databinding.CalculateResultExportBinding

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
        val binding = if (isExport) CalculateResultExportBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ) else CalculateResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalculateResultViewHolder(
            binding,
            onItemInspect,
            onItemExport,
            onItemDelete
        )
    }

    override fun onBindViewHolder(holder: CalculateResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CalculateResultViewHolder(
        private val binding: ViewBinding,
        private val onItemInspect: (CalculateResult) -> Unit,
        private val onItemExport: (CalculateResult) -> Unit,
        private val onItemDelete: (CalculateResult) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(calculateResult: CalculateResult) {
            if (binding is CalculateResultBinding) {
                binding.apply {
                    tvName.text = calculateResult.name
                    btnInspect.setOnClickListener {
                        onItemInspect(calculateResult)
                    }
                    imgDelete.setOnClickListener {
                        onItemDelete(calculateResult)
                    }
                }
            } else if (binding is CalculateResultExportBinding) {
                binding.apply {
                    tvName.text = calculateResult.name
                    btnInspect.setOnClickListener {
                        onItemInspect(calculateResult)
                    }
                    btnExport.setOnClickListener {
                        onItemExport(calculateResult)
                    }
                    imgDelete.setOnClickListener {
                        onItemDelete(calculateResult)
                    }
                }
            }
        }
    }
}