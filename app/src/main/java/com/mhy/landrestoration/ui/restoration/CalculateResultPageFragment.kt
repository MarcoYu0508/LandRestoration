package com.mhy.landrestoration.ui.restoration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mhy.landrestoration.adapter.CalculateResultListAdapter
import com.mhy.landrestoration.databinding.FragmentCalculateResultPageBinding
import com.mhy.landrestoration.enums.RestorationType
import com.mhy.landrestoration.viewmodels.CoordinateListViewModel

class CalculateResultPageFragment(private val restorationType: RestorationType) : Fragment() {

    private var binding: FragmentCalculateResultPageBinding? = null

    private val coordinateListViewModel: CoordinateListViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this,
            CoordinateListViewModel.Factory(activity.application)
        )[CoordinateListViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentCalculateResultPageBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calculateResultListAdapter = CalculateResultListAdapter(
            onItemInspect = {},
            onItemExport = {},
            onItemDelete = {},
            isExport = false
        )

        binding?.apply {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = calculateResultListAdapter
        }

        coordinateListViewModel.getCalculateResultsByType(restorationType.tag)
            .observe(viewLifecycleOwner) {
                calculateResultListAdapter.submitList(it)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}