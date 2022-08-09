package com.mhy.landrestoration.ui.restoration

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mhy.landrestoration.R
import com.mhy.landrestoration.database.model.Project
import com.mhy.landrestoration.enums.RestorationType
import com.mhy.landrestoration.enums.SelectPointDisplayType
import com.mhy.landrestoration.util.ShowAlert
import com.mhy.landrestoration.util.hideKeyboard
import com.mhy.landrestoration.viewmodels.CoordinateListViewModel
import com.mhy.landrestoration.viewmodels.CoordinateSelectViewModel
import kotlinx.coroutines.launch


abstract class RestorationPageFragment : Fragment() {

    protected val showAlert = ShowAlert()

    private var checked: BooleanArray? = null

    protected val coordinateListViewModel: CoordinateListViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this,
            CoordinateListViewModel.Factory(activity.application)
        )[CoordinateListViewModel::class.java]
    }

    protected val coordinateSelectViewModel: CoordinateSelectViewModel by activityViewModels()

    abstract fun saveResult()

    abstract fun calculate()

    abstract fun restorationType(): RestorationType

    private fun selectPointFromList() {

    }

    private fun selectPointFromMap() {
        hideKeyboard()
        findNavController().navigate(R.id.action_distanceFragment_to_selectPointMapFragment)
    }

    protected fun showProjectSelectDialog(type: SelectPointDisplayType) {
        lifecycleScope.launch {
            val projects = coordinateListViewModel.getProjectsSync()
            val projectNames =
                projects.map { project -> project.name }.toTypedArray()

            val selected = mutableListOf<Project>()
            if (checked == null) {
                checked = BooleanArray(projectNames.size)
            } else {
                checked?.let {
                    for (i in it.indices) {
                        if (it[i]) selected.add(projects[i])
                    }
                }
            }
            showAlert.showMultipleChoice(
                requireContext(),
                "選擇專案: ${type.tag}",
                projectNames,
                checked!!,
                { _, which, isChecked ->
                    if (isChecked) {
                        checked?.let {
                            selected.add(projects[which])
                            it[which] = true
                        }
                    } else {
                        checked?.let {
                            selected.remove(projects[which])
                            it[which] = false
                        }
                    }
                },
                "確定",
                { dialog, _ ->
                    if (selected.size == 0) {
                        showAlert.show(requireContext(), "錯誤", "請選擇專案")
                        return@showMultipleChoice
                    }
                    coordinateSelectViewModel.setSelectedProjects(selected)
                    if (type == SelectPointDisplayType.Map) {
                        selectPointFromMap()
                    } else if (type == SelectPointDisplayType.List) {
                        selectPointFromList()
                    }
                    dialog.dismiss()
                },
                "取消"
            )
        }
    }
}