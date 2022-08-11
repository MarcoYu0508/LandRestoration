package com.mhy.landrestoration.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mhy.landrestoration.R
import com.mhy.landrestoration.databinding.PointSelectItemBinding
import com.mhy.landrestoration.model.SelectPointListItem

class SelectPointItemListAdapter :
    RecyclerView.Adapter<SelectPointItemListAdapter.SelectPointItemAdapter>(), Filterable {

    var items: List<SelectPointListItem> = emptyList()
        set(value) {
            field = value
            filteredItems.addAll(value)
            notifyDataSetChanged()
        }

    var filteredItems: MutableList<SelectPointListItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectPointItemAdapter {
        val binding: PointSelectItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.point_select_item,
            parent, false
        )
        return SelectPointItemAdapter(binding, onSelect)
    }

    var onSelect: ((SelectPointListItem) -> Unit)? = null

    override fun onBindViewHolder(holder: SelectPointItemAdapter, position: Int) {
        holder.bind(filteredItems[position])
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val filteredList = mutableListOf<SelectPointListItem>()
                if (constraint == null || constraint.isEmpty()) filteredList.addAll(items)
                else filteredList.addAll(items.filter { item ->
                    item.name.lowercase().contains(
                        constraint.toString().lowercase(), ignoreCase = true
                    )
                })
                val filterResult = FilterResults()
                filterResult.values = filteredList
                return filterResult
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                filteredItems.clear()
                if (results != null) {
                    filteredItems.addAll(results.values as List<SelectPointListItem>)
                    notifyDataSetChanged()
                }
            }
        }
    }

    inner class SelectPointItemAdapter(
        private val binding: PointSelectItemBinding,
        private val onSelect: ((SelectPointListItem) -> Unit)?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SelectPointListItem) {
            binding.apply {
                tvName.text = item.name
                tvN.text = item.N.toString()
                tvE.text = item.E.toString()
                radBtn.isChecked = item.isSelected
                radBtn.setOnClickListener {
                    onSelect?.invoke(item)
                }
            }
        }
    }
}
