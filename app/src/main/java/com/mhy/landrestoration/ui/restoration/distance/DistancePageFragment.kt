package com.mhy.landrestoration.ui.restoration.distance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mhy.landrestoration.R
import com.mhy.landrestoration.adapter.SelectedPointItemAdapter
import com.mhy.landrestoration.database.model.CalculateResult
import com.mhy.landrestoration.databinding.FragmentDistancePageBinding
import com.mhy.landrestoration.enums.RestorationType
import com.mhy.landrestoration.enums.SelectPointDisplayType
import com.mhy.landrestoration.model.SelectedPointItem
import com.mhy.landrestoration.ui.restoration.RestorationPageFragment
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

private const val TAG = "DistancePageFragment"

class DistancePageFragment : RestorationPageFragment() {

    private var binding: FragmentDistancePageBinding? = null

    private lateinit var selectedPointItemAdapter: SelectedPointItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentDistancePageBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            coordinateListViewModel.setCalculateResult(null)
            coordinateSelectViewModel.setSelectedPointItems(listOf())
            findNavController().popBackStack()
        }

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedPointItemAdapter =
            SelectedPointItemAdapter(mapChoose = { index ->
                coordinateSelectViewModel.setSelectedIndex(index)
                showProjectSelectDialog(SelectPointDisplayType.Map)
            }, listChoose = { index ->
                coordinateSelectViewModel.setSelectedIndex(index)
                showProjectSelectDialog(SelectPointDisplayType.List)
            }, onItemDelete = null)


        binding?.apply {
            lifecycleOwner = viewLifecycleOwner

            fragment = this@DistancePageFragment

            recyclerView.layoutManager = LinearLayoutManager(context)

            recyclerView.adapter = selectedPointItemAdapter
        }

        coordinateSelectViewModel.selectedPointItems.observe(viewLifecycleOwner) {
            selectedPointItemAdapter.submitList(it)
        }

        if (coordinateSelectViewModel.selectedPointItems.value?.size == 0) {
            val items = listOf(
                SelectedPointItem(title = "坐標點A", name = null, N = null, E = null),
                SelectedPointItem(title = "坐標點B", name = null, N = null, E = null)
            )
            coordinateSelectViewModel.setSelectedPointItems(items)
        }

        coordinateListViewModel.saveResultErrorMessage.observe(viewLifecycleOwner) {
            showAlert.show(requireContext(), "錯誤", it)
        }
    }

    override fun saveResult() {
        val resultJson = coordinateListViewModel.calculateResult.value
        if (resultJson == null) {
            showAlert.show(requireContext(), "錯誤", "尚未有計算結果");
        } else {
            val inputView = layoutInflater.inflate(R.layout.dialog_input, null)
            val input: EditText = inputView.findViewById(R.id.etInput)
            input.hint = "請輸入成果名稱"
            showAlert.show(
                requireContext(),
                inputView,
                "請輸入成果名稱",
                null,
                "確定",
                { alertDialog, _ ->
                    if (input.text.toString() == "") {
                        input.error = "未輸入成果名稱"
                    } else {
                        val name = input.text.toString()
                        val calculateResult = CalculateResult(
                            name = name,
                            type = restorationType().tag,
                            result = resultJson.toString()
                        )
                        coordinateListViewModel.createCalculateResult(calculateResult)
                        alertDialog.dismiss()
                    }
                },
                "取消",
                R.drawable.ic_baseline_add_24_blue
            )
        }
    }

    override fun calculate() {
        val items = selectedPointItemAdapter.currentList

        if (items.size != 2) {
            showAlert.show(requireContext(), "錯誤", "請重新進入頁面")
            findNavController().popBackStack()
        } else {
            var errorMsg = ""
            for (item in items) {
                if (item.N == null) errorMsg += "${item.title}: N\n"
                if (item.E == null) errorMsg += "${item.title}: E"
            }

            if (errorMsg.isNotEmpty()) {
                showAlert.show(requireContext(), "欄位未輸入", errorMsg)
                return
            }

            val dx = items[0].E!! - items[1].E!!
            val dy = items[0].N!! - items[1].N!!
            val length = (sqrt(dx.pow(2) + dy.pow(2)) * 1000.0).roundToInt() / 1000.0
            binding?.apply {
                etLength.setText(length.toString())
            }
            val resultJson = JSONObject()
            resultJson.put("L", length)
            val points = JSONArray()
            for (item in items) {
                val point = JSONObject()
                point
                    .put("title", item.title)
                    .put("name", item.name)
                    .put("N", item.N)
                    .put("E", item.E)
                points.put(point)
            }
            resultJson.put("points", points)
            coordinateListViewModel.setCalculateResult(resultJson)
        }
    }

    override fun restorationType() = RestorationType.Distance

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}