package com.mhy.landrestoration.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mhy.landrestoration.adapter.SelectPointItemListAdapter
import com.mhy.landrestoration.databinding.FragmentSelectPointListBinding
import com.mhy.landrestoration.model.SelectPointListItem
import com.mhy.landrestoration.util.ShowAlert
import com.mhy.landrestoration.viewmodels.CoordinateListViewModel
import com.mhy.landrestoration.viewmodels.CoordinateSelectViewModel
import kotlinx.coroutines.launch

class SelectPointListFragment : Fragment() {
    private var binding: FragmentSelectPointListBinding? = null

    private val coordinateSelectViewModel: CoordinateSelectViewModel by activityViewModels()

    private val showAlert = ShowAlert()

    private var selectedItem: SelectPointListItem? = null

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
        val fragmentBinding = FragmentSelectPointListBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectPointItemListAdapter = SelectPointItemListAdapter()

        selectPointItemListAdapter.onSelect = { item ->
            if (selectedItem != null) {
                selectedItem?.isSelected = false
                selectPointItemListAdapter.notifyItemChanged(
                    selectPointItemListAdapter.items.indexOf(
                        selectedItem
                    )
                )
            }
            selectedItem = item
            selectedItem?.isSelected = true
            selectPointItemListAdapter.notifyItemChanged(
                selectPointItemListAdapter.items.indexOf(
                    selectedItem
                )
            )
        }

        binding?.apply {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = selectPointItemListAdapter

            val searchView = topAppBar.menu.getItem(0).actionView as SearchView
            searchView.setOnCloseListener {
                Toast.makeText(requireContext(), "close", Toast.LENGTH_SHORT).show();
                true
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    selectPointItemListAdapter.filter.filter(query)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText?.isEmpty() == true) {
                        selectPointItemListAdapter.filter.filter(newText)
                    }
                    return false
                }
            })

            btnSelect.setOnClickListener {
                if (selectedItem != null) {
                    lifecycleScope.launch {
                        val coordinate =
                            coordinateListViewModel.getCoordinateByIdSync(selectedItem!!.id)
                        coordinateSelectViewModel.setSelectedCoordinate(coordinate)
                        findNavController().popBackStack()
                    }
                } else {
                    showAlert.show(requireContext(), "錯誤", "未選擇點位")
                }
            }
        }


        lifecycleScope.launch {
            val projects = coordinateSelectViewModel.selectedProjects.value
            if (projects != null) {
                val coordinates =
                    coordinateListViewModel.getCoordinatesByProjectIdsSync(projects.map { project -> project.id })
                selectPointItemListAdapter.items = coordinates.map { coordinate ->
                    SelectPointListItem(
                        coordinate.id,
                        coordinate.name,
                        coordinate.N,
                        coordinate.E
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}