package com.mhy.landrestoration.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mhy.landrestoration.R
import com.mhy.landrestoration.adapter.PointListAdapter
import com.mhy.landrestoration.database.coordinate.Coordinate
import com.mhy.landrestoration.database.coordinate.Project
import com.mhy.landrestoration.databinding.FragmentPointListBinding
import com.mhy.landrestoration.util.ShowAlert
import com.mhy.landrestoration.viewmodels.CoordinateListViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

private const val TAG = "PointListFragment"
private const val REIMPORT = "reimport"
private const val IMPORT = "import"

/**
 * A simple [Fragment] subclass.
 * Use the [PointListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PointListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = PointListFragment()
    }

    private var binding: FragmentPointListBinding? = null

    private val args by navArgs<PointListFragmentArgs>()

    private lateinit var project: Project

    private var pointListAdapter: PointListAdapter? = null

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

    private lateinit var fileResultLauncher: ActivityResultLauncher<Intent>

    private var chooseFileIntentType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        project = args.project

        fileResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                it.data?.let { intent ->
                    intent.data?.apply {
                        val coordinates = getCoordinatesFromFile(this)
                        when (chooseFileIntentType) {
                            IMPORT -> {
                                coordinateListViewModel.createAllCoordinates(coordinates)
                            }
                            REIMPORT -> {
                                coordinateListViewModel.refreshProjectCoordinates(
                                    project.id,
                                    coordinates
                                )
                            }
                        }
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentPointListBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pointListAdapter = PointListAdapter(
            onItemEdit = {
                updateCoordinate(it)
            },
            onItemDelete = {
                showAlert.show(
                    requireContext(), "刪除點位", "請問要刪除點位${it.name}嗎?",
                    "確定", { _, _ ->
                        coordinateListViewModel.deleteCoordinate(it)
                    }, "取消", null
                )
            }
        )

        binding?.apply {
            topAppBar.title = "點位列表: " + (project?.name ?: "")
            topAppBar.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_pointListFragment_to_projectListFragment)
            }
            topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.append -> {
                        appendCoordinateDialog()
                        true
                    }
                    R.id.reimport -> {
                        chooseFileIntentType = REIMPORT
                        chooseFile()
                        true
                    }
                    else -> false
                }
            }
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = pointListAdapter
        }

        coordinateListViewModel.getCoordinatesByProjectId(project.id)
            .observe(viewLifecycleOwner) { points ->
                points?.apply {
                    pointListAdapter?.submitList(points)
                }
            }

        coordinateListViewModel.insertErrorMessage.observe(viewLifecycleOwner) {
            showAlert.show(requireContext(), "錯誤", it)
        }
    }

    private fun appendCoordinateDialog() {
        val methods = arrayOf("填寫新增", "檔案匯入")
        showAlert.showSingleChoice(requireContext(), "新增方式", methods, { dialog, index ->
            when (methods[index]) {
                "填寫新增" -> {
                    appendSingleCoordinate()
                    dialog.dismiss()
                }
                "檔案匯入" -> {
                    chooseFileIntentType = IMPORT
                    chooseFile()
                    dialog.dismiss()
                }
            }
        }, R.drawable.ic_baseline_add_24_blue)
    }

    private fun appendSingleCoordinate() {
        val inputView = layoutInflater.inflate(R.layout.dialog_point_form, null)
        val etName: EditText = inputView.findViewById(R.id.et_name)
        val etN: EditText = inputView.findViewById(R.id.et_n)
        val etE: EditText = inputView.findViewById(R.id.et_e)
        showAlert.show(
            requireContext(),
            inputView,
            "請輸入專案名稱",
            null,
            "確定",
            { alertDialog, _ ->
                if (etName.text.toString() == "") {
                    etName.error = "未輸入點名"
                    return@show
                }
                if (etN.text.toString() == "") {
                    etN.error = "未輸入N"
                    return@show
                }
                if (etE.text.toString() == "") {
                    etE.error = "未輸入E"
                    return@show
                }
                coordinateListViewModel.createCoordinate(
                    Coordinate(
                        project_id = project.id,
                        name = etName.text.toString(),
                        N = etN.text.toString().toDouble(),
                        E = etE.text.toString().toDouble()
                    )
                )
                alertDialog.dismiss()
            },
            "取消",
            R.drawable.ic_baseline_add_24_blue
        )
    }

    private fun updateCoordinate(coordinate: Coordinate) {
        val inputView = layoutInflater.inflate(R.layout.dialog_point_form, null)
        val etName: EditText = inputView.findViewById(R.id.et_name)
        etName.setText(coordinate.name)
        val etN: EditText = inputView.findViewById(R.id.et_n)
        etN.setText(coordinate.N.toString())
        val etE: EditText = inputView.findViewById(R.id.et_e)
        etE.setText(coordinate.E.toString())
        showAlert.show(
            requireContext(),
            inputView,
            "請輸入專案名稱",
            null,
            "確定",
            { alertDialog, _ ->
                if (etName.text.toString() == "") {
                    etName.error = "未輸入點名"
                    return@show
                }
                if (etN.text.toString() == "") {
                    etN.error = "未輸入N"
                    return@show
                }
                if (etE.text.toString() == "") {
                    etE.error = "未輸入E"
                    return@show
                }

                val updateCoordinate = Coordinate(
                    id = coordinate.id,
                    project_id = coordinate.project_id,
                    name = etName.text.toString(),
                    N = etN.text.toString().toDouble(),
                    E = etE.text.toString().toDouble()
                )

                coordinateListViewModel.updateCoordinate(updateCoordinate)
                alertDialog.dismiss()
            },
            "取消",
            R.drawable.ic_baseline_update_24_blue
        )
    }

    private fun chooseFile() {
        val uri = Uri.parse("content://com.android.externalstorage.documents/document/")
        val extraMimeTypes =
            arrayOf("application/octet-stream", "text/comma-separated-values", "text/plain")
        val intent: Intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent()
                .setAction(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("*/*")
                .putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri) //指定路徑
                .putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes)
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        } else {
            Intent(Intent.ACTION_OPEN_DOCUMENT, uri)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes)
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        fileResultLauncher.launch(intent)
    }

    private fun getCoordinatesFromFile(uri: Uri): List<Coordinate> {
        val coordinates = mutableListOf<Coordinate>()
        val cr = requireContext().contentResolver
        val cursor = cr.query(uri, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            val fileName = cursor.getString(nameIndex)
            val extension = fileName.substring(fileName.lastIndexOf(".") + 1)
            if (extension.lowercase() != "csv" && extension.lowercase() != "ctl" && extension.lowercase() != "txt") {
                showAlert.show(requireContext(), "檔案格式錯誤", "只接受csv, ctl, txt")
                cursor.close()
                return coordinates
            }
            cursor.close()
            val inputStream = cr.openInputStream(uri)
            val bufferReader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (bufferReader.readLine().also { line = it } != null) {
                line?.let {
                    createCoordinateFromLine(
                        it,
                        extension.lowercase()
                    )?.let { coordinate -> coordinates.add(coordinate) }
                }
            }
            bufferReader.close()
        }
        return coordinates
    }

    private fun createCoordinateFromLine(line: String, extension: String): Coordinate? {
        if (extension == "csv") {
            val point = line.split(",".toRegex()).toTypedArray()
            if (point.size >= 3) return Coordinate(
                project_id = project.id,
                name = point[0],
                N = point[1].toDouble(),
                E = point[2].toDouble(),
            )
        } else if (extension == "ctl" || extension == "txt") {
            val point = line.split("\\s+".toRegex()).toTypedArray()
            if (point.size >= 3) return Coordinate(
                project_id = project.id,
                name = point[0],
                N = point[1].toDouble(),
                E = point[2].toDouble(),
            )
        }
        return null
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}