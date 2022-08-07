package com.mhy.landrestoration.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mhy.landrestoration.databinding.PointSelectLayoutBinding
import com.mhy.landrestoration.model.SelectedPointItem

class SelectedPointItemAdapter(
    private val items: List<SelectedPointItem>,
    private val mapChoose: (SelectedPointItem) -> Unit,
    private val listChoose: (SelectedPointItem) -> Unit,
    private val onItemDelete: (SelectedPointItem) -> Unit
) :
    RecyclerView.Adapter<SelectedPointItemAdapter.SelectedPointViewHolder>() {

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
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class SelectedPointViewHolder(
        private var binding: PointSelectLayoutBinding,
        private val mapChoose: (SelectedPointItem) -> Unit,
        private val listChoose: (SelectedPointItem) -> Unit,
        private val onItemDelete: (SelectedPointItem) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SelectedPointItem) {
            binding.apply {
                title.text = item.title
                item.name?.let {
                    etPointName.setText(it)
                }
                item.N?.let {
                    etN.setText(it.toString())
                }
                item.E?.let {
                    etE.setText(it.toString())
                }
                imgMapChoose.setOnClickListener {
                    mapChoose(item)
                }
                imgListChoose.setOnClickListener {
                    listChoose(item)
                }
                imgRemove.setOnClickListener {
                    onItemDelete(item)
                }
            }
        }
    }
}