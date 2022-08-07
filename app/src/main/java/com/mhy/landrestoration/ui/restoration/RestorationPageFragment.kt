package com.mhy.landrestoration.ui.restoration

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mhy.landrestoration.R
import com.mhy.landrestoration.database.coordinate.Project
import com.mhy.landrestoration.enums.SelectPointDisplayType
import com.mhy.landrestoration.util.ShowAlert
import com.mhy.landrestoration.viewmodels.CoordinateListViewModel
import com.mhy.landrestoration.viewmodels.CoordinateSelectViewModel
import kotlinx.coroutines.launch


abstract class RestorationPageFragment : Fragment() {

    protected val showAlert = ShowAlert()

    protected var checked: BooleanArray? = null

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

    protected fun selectPointFromList() {
        showProjectSelectDialog(SelectPointDisplayType.List)
    }

    protected fun selectPointFromMap() {
        showProjectSelectDialog(SelectPointDisplayType.Map)
    }

    private fun showProjectSelectDialog(type: SelectPointDisplayType) {
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
                "選擇專案",
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
                        showAlert.show(requireContext(),"錯誤","請選擇專案")
                        return@showMultipleChoice
                    }
                    coordinateSelectViewModel.setSelectedProjects(selected)
                    findNavController().navigate(R.id.action_distanceFragment_to_selectPointMapFragment)
                    dialog.dismiss()
                },
                "取消"
            )
        }

    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_restoration_page, container, false)
//    }
}