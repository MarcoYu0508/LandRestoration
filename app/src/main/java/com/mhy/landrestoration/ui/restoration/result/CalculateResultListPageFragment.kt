package com.mhy.landrestoration.ui.restoration.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mhy.landrestoration.R
import com.mhy.landrestoration.adapter.CalculateResultListAdapter
import com.mhy.landrestoration.database.model.CalculateResult
import com.mhy.landrestoration.database.model.Coordinate
import com.mhy.landrestoration.databinding.FragmentCalculateResultListPageBinding
import com.mhy.landrestoration.enums.RestorationType
import com.mhy.landrestoration.util.ShowAlert
import com.mhy.landrestoration.viewmodels.CoordinateListViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CalculateResultListPageFragment(
    private val restorationType: RestorationType,
    private val isExport: Boolean = false
) : Fragment() {

    private var binding: FragmentCalculateResultListPageBinding? = null

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val fragmentBinding =
            FragmentCalculateResultListPageBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calculateResultListAdapter = CalculateResultListAdapter(
            onItemInspect = {
                val dialog =
                    BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
                val layout = layoutInflater.inflate(R.layout.calculate_result_dialog, null)
                val tvContent: TextView = layout.findViewById(R.id.tvContent)
                tvContent.text = getResultContent(it)
                dialog.setContentView(layout)
                dialog.setCancelable(true)
                dialog.show()
            },
            onItemExport = {
                lifecycleScope.launch {
                    val projects = coordinateListViewModel.getProjectsSync()
                    val projectNames =
                        projects.map { project -> project.name }.toTypedArray()

                    showAlert.showSingleChoice(
                        requireContext(),
                        "選擇專案",
                        projectNames,
                        { dialog, index ->
                            val project = projects[index]
                            try {
                                val resultJson = JSONObject(it.result)
                                val n = resultJson.getDouble("N")
                                val e = resultJson.getDouble("E")
                                val name = it.name
                                coordinateListViewModel.createCoordinate(
                                    Coordinate(
                                        project_id = project.id,
                                        name = name,
                                        N = n,
                                        E = e
                                    )
                                )
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            dialog.dismiss()
                        },
                        R.drawable.ic_baseline_add_24_blue
                    )
                }
            },
            onItemDelete = {
                showAlert.show(
                    requireContext(), "刪除成果", "請問要刪除成果${it.name}嗎?",
                    "確定", { _, _ ->
                        coordinateListViewModel.deleteCalculateResult(it)
                    }, "取消", null
                )
            },
            isExport = isExport
        )

        binding?.apply {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = calculateResultListAdapter
        }

        coordinateListViewModel.getCalculateResultsByType(restorationType.tag)
            .observe(viewLifecycleOwner) {
                calculateResultListAdapter.submitList(it)
            }

        coordinateListViewModel.insertErrorMessage.observe(viewLifecycleOwner) {
            showAlert.show(requireContext(), "錯誤", it)
        }
    }

    private fun getResultContent(result: CalculateResult): String {
        var content = ""
        when (restorationType) {
            RestorationType.Distance -> {
                try {
                    val resultJson = JSONObject(result.result)
                    content += "名稱: ${result.name}\n"
                    content += "結果:\n   距離: ${resultJson.getString("L")}m\n"
                    val points: JSONArray = resultJson.getJSONArray("points")
                    for (i in 0 until points.length()) {
                        val point = points.getJSONObject(i)
                        content += "${point.getString("title")}\n"
                        content += "  點名: ${point.getString("name")}\n"
                        content += "   N: ${point.getString("N")}\n"
                        content += "   E: ${point.getString("E")}\n"
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            RestorationType.Radiation -> {
                try {
                    val resultJson = JSONObject(result.result)
                    content += "名稱: ${result.name}\n"
                    content += "結果:\n   N: ${resultJson.getString("N")}\n   E: ${
                        resultJson.getString(
                            "E"
                        )
                    }\n"
                    content += "線段L: ${resultJson.getString("L")}\n角度⍺: ${resultJson.getString("⍺")}\n"
                    val points: JSONArray = resultJson.getJSONArray("points")
                    for (i in 0 until points.length()) {
                        val point = points.getJSONObject(i)
                        content += "${point.getString("title")}\n"
                        content += "  點名: ${point.getString("name")}\n"
                        content += "   N: ${point.getString("N")}\n"
                        content += "   E: ${point.getString("E")}\n"
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        return content
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}