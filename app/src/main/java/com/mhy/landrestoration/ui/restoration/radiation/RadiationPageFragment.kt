package com.mhy.landrestoration.ui.restoration.radiation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mhy.landrestoration.R
import com.mhy.landrestoration.adapter.SelectedPointItemAdapter
import com.mhy.landrestoration.database.model.CalculateResult
import com.mhy.landrestoration.databinding.FragmentRadiationPageBinding
import com.mhy.landrestoration.enums.RestorationType
import com.mhy.landrestoration.enums.SelectPointDisplayType
import com.mhy.landrestoration.model.SelectedPointItem
import com.mhy.landrestoration.ui.restoration.RestorationPageFragment
import com.mhy.landrestoration.util.CoordinateUtil.convertToAngleDisplayText
import com.mhy.landrestoration.util.CoordinateUtil.fromDisplayAngleStringToDegrees
import com.mhy.landrestoration.util.hideKeyboard
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


class RadiationPageFragment : RestorationPageFragment() {

    private var binding: FragmentRadiationPageBinding? = null

    private lateinit var selectedPointItemAdapter: SelectedPointItemAdapter

    private var angleStr = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentRadiationPageBinding.inflate(layoutInflater, container, false)
        binding = fragmentBinding

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedPointItemAdapter =
            SelectedPointItemAdapter(
                mapChoose = { index ->
                    coordinateSelectViewModel.setSelectedIndex(index)
                    showProjectSelectDialog(SelectPointDisplayType.Map)
                }, listChoose = { index ->
                    coordinateSelectViewModel.setSelectedIndex(index)
                    showProjectSelectDialog(SelectPointDisplayType.List)
                }, onItemDelete = null
            )


        binding?.apply {
            lifecycleOwner = viewLifecycleOwner

            fragment = this@RadiationPageFragment

            recyclerView.layoutManager = LinearLayoutManager(context)

            recyclerView.adapter = selectedPointItemAdapter

            etAngle.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    angleStr = etAngle.text.toString()
                    if (angleStr != "") {
                        val convert: String = convertToAngleDisplayText(angleStr)
                        etAngle.setText(convert)
                    }
                } else {
                    val angle = etAngle.text.toString()
                    if (angle != "") {
                        etAngle.setText(angleStr)
                    }
                }
            }
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

            var length = 0.0
            var displayAngleStr = ""
            var angle = 0.0
            binding?.apply {
                if (etL.text?.isEmpty() == true) errorMsg += "線段L\n"
                else length = etL.text.toString().toDouble()
                if (etAngle.text?.isEmpty() == true) errorMsg += "角度⍺\n"
                else {
                    angle = if (etAngle.hasFocus()) {
                        displayAngleStr = convertToAngleDisplayText(etAngle.text.toString())
                        fromDisplayAngleStringToDegrees(displayAngleStr)
                    } else {
                        displayAngleStr = etAngle.text.toString()
                        fromDisplayAngleStringToDegrees(displayAngleStr)
                    }
                }
            }

            for (item in items) {
                if (item.N == null) errorMsg += "${item.title}: N\n"
                if (item.E == null) errorMsg += "${item.title}: E\n"
            }

            if (errorMsg.isNotEmpty()) {
                showAlert.show(requireContext(), "欄位未輸入", errorMsg)
                return
            }

            val ax = items[0].E!!
            val ay = items[0].N!!
            val bx = items[1].E!!
            val by = items[1].N!!
            val dx = bx - ax
            val dy = by - ay
            val beta = atan(dx / dy)
            val alpha = Math.toRadians(angle)
            val e = ((ax + length * sin(alpha + beta)) * 1000.0).roundToInt() / 1000.0
            val n = ((ay + length * cos(alpha + beta) * 1000.0)).roundToInt() / 1000.0

            binding?.apply {
                etE.setText(e.toString())
                etN.setText(n.toString())
            }

            val resultJson = JSONObject()
            resultJson.put("L", length)
            resultJson.put("⍺", displayAngleStr)
            resultJson.put("N", n)
            resultJson.put("E", e)
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

            binding?.scrollView?.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun restorationType() = RestorationType.Radiation

    override fun selectPointFromList() {
        hideKeyboard()
        findNavController().navigate(R.id.action_radiationFragment_to_selectPointListFragment)
    }

    override fun selectPointFromMap() {
        hideKeyboard()
        findNavController().navigate(R.id.action_radiationFragment_to_selectPointMapFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}