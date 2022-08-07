package com.mhy.landrestoration.ui.restoration.distance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mhy.landrestoration.R
import com.mhy.landrestoration.adapter.SelectedPointItemAdapter
import com.mhy.landrestoration.databinding.FragmentDistancePageBinding
import com.mhy.landrestoration.model.SelectedPointItem
import com.mhy.landrestoration.ui.restoration.RestorationPageFragment
import kotlinx.coroutines.launch

class DistancePageFragment : RestorationPageFragment() {

    private var binding: FragmentDistancePageBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentDistancePageBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = listOf(
            SelectedPointItem(title = "坐標點A", name = null, N = null, E = null),
            SelectedPointItem(title = "坐標點B", name = null, N = null, E = null),
        )

        val selectedPointItemAdapter =
            SelectedPointItemAdapter(items, mapChoose = { item ->
                selectPointFromMap()
            }, listChoose = { item ->
                selectPointFromList()
            }, onItemDelete = { item -> })

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner

            fragment = this@DistancePageFragment

            recyclerView.layoutManager = LinearLayoutManager(context)

            recyclerView.adapter = selectedPointItemAdapter
        }
    }

    override fun saveResult() {
    }

    override fun calculate() {

    }
}