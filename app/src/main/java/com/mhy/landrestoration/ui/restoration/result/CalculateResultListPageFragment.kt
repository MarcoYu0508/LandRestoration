package com.mhy.landrestoration.ui.restoration.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mhy.landrestoration.R
import com.mhy.landrestoration.adapter.CalculateResultListAdapter
import com.mhy.landrestoration.database.model.CalculateResult
import com.mhy.landrestoration.databinding.FragmentCalculateResultListPageBinding
import com.mhy.landrestoration.enums.RestorationType
import com.mhy.landrestoration.viewmodels.CoordinateListViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CalculateResultListPageFragment(private val restorationType: RestorationType) : Fragment() {

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
        }
        return content
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}