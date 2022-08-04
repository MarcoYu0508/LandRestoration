package com.mhy.landrestoration.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mhy.landrestoration.R
import com.mhy.landrestoration.adapter.ProjectListAdapter
import com.mhy.landrestoration.database.coordinate.Project
import com.mhy.landrestoration.databinding.FragmentProjectListBinding
import com.mhy.landrestoration.util.ShowAlert
import com.mhy.landrestoration.viewmodels.CoordinateListViewModel

private const val TAG = "ProjectListFragment"

/**
 * A simple [Fragment] subclass.
 * Use the [ProjectListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProjectListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = ProjectListFragment()
    }

    private var binding: FragmentProjectListBinding? = null

    private var projectListAdapter: ProjectListAdapter? = null

    private val coordinateListViewModel: CoordinateListViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this,
            CoordinateListViewModel.Factory(activity.application)
        )[CoordinateListViewModel::class.java]
    }

    private val showAlert = ShowAlert()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentProjectListBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        projectListAdapter = ProjectListAdapter(
            onItemInspect = {
                val action =
                    ProjectListFragmentDirections.actionProjectListFragmentToPointListFragment(
                        project = it
                    )
                findNavController().navigate(action)
            }, onItemExport = {
                val types = arrayOf("csv", "txt", "ctl", "json")
                showAlert.showSingleChoice(requireContext(), "輸出檔案種類", types, { dialog, index ->
                    val type = types[index]
                    exportProject(it, type)
                    dialog.dismiss()
                }, R.drawable.ic_baseline_add_24_blue)
            }, onItemDelete = {
                showAlert.show(
                    requireContext(), "刪除專案", "請問要刪除專案${it.name}嗎?",
                    "確定", { _, _ ->
                        coordinateListViewModel.deleteProject(it)
                    }, "取消", null
                )
            })

        binding?.apply {
            topAppBar.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_projectListFragment_to_entryFragment)
            }
            topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.append -> {
                        appendProject()
                        true
                    }
                    else -> false
                }
            }
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = projectListAdapter
        }

        coordinateListViewModel.projects.observe(viewLifecycleOwner) { projects ->
            projects?.apply {
                projectListAdapter?.submitList(projects)
            }
        }

        coordinateListViewModel.insertErrorMessage.observe(viewLifecycleOwner) {
            showAlert.show(requireContext(), "錯誤", it)
        }

        coordinateListViewModel.exportErrorMessage.observe(viewLifecycleOwner) {
            showAlert.show(requireContext(), "錯誤", it)
        }

        coordinateListViewModel.exportPath.observe(viewLifecycleOwner) {
            showAlert.show(
                requireContext(),
                "專案導出",
                "成功導出至下載\n路徑: $it",
                R.drawable.ic_baseline_done_24_bule
            )
        }
    }

    private fun appendProject() {
        val inputView = layoutInflater.inflate(R.layout.dialog_input, null)
        val input: EditText = inputView.findViewById(R.id.et_input)
        showAlert.show(
            requireContext(),
            inputView,
            "請輸入專案名稱",
            null,
            "確定",
            { alertDialog, _ ->
                if (input.text.toString() == "") {
                    input.error = "未輸入專案名稱"
                } else {
                    coordinateListViewModel.createProject(input.text.toString())
                    alertDialog.dismiss()
                }
            },
            "取消",
            R.drawable.ic_baseline_add_24_blue
        )
    }

    private fun exportProject(project: Project, type: String) {
        coordinateListViewModel.saveCoordinatesToFile(project, type)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}