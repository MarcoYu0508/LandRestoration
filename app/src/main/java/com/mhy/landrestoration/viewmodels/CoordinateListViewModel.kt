package com.mhy.landrestoration.viewmodels

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.*
import com.google.gson.GsonBuilder
import com.mhy.landrestoration.database.AppDatabase
import com.mhy.landrestoration.database.model.CalculateResult
import com.mhy.landrestoration.database.model.Coordinate
import com.mhy.landrestoration.database.model.Project
import com.mhy.landrestoration.database.model.asOutputModel
import com.mhy.landrestoration.repository.CalculateResultRepository
import com.mhy.landrestoration.repository.CoordinateRepository
import com.mhy.landrestoration.repository.ProjectRepository
import com.mhy.landrestoration.util.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val TAG = "CoordinateListViewModel"

class CoordinateListViewModel(application: Application) : AndroidViewModel(application) {
    private val projectRepository = ProjectRepository(AppDatabase.getDatabase(application))
    private val coordinateRepository = CoordinateRepository(AppDatabase.getDatabase(application))
    private val calculateResultRepository =
        CalculateResultRepository(AppDatabase.getDatabase(application))

    private val _insertErrorMessage = MutableLiveData<String>()
    val insertErrorMessage: LiveData<String> = _insertErrorMessage

    val projects = projectRepository.projects

    suspend fun getProjectsSync() = projectRepository.getProjectsSync()

    fun createProject(name: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    projectRepository.createSync(Project(name = name))
                } catch (e: SQLiteConstraintException) {
                    _insertErrorMessage.postValue("此專案已存在")
                }
            }
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                coordinateRepository.deleteByProjectIdSync(project.id)
                projectRepository.deleteSync(project)
            }
        }
    }

    fun getCoordinatesByProjectId(projectId: Int) =
        coordinateRepository.getCoordinatesByProjectId(projectId)

    suspend fun getCoordinatesByProjectIdSync(projectId: Int) =
        coordinateRepository.getCoordinatesByProjectIdSync(projectId)

    suspend fun getCoordinatesByProjectIdsSync(projectIds: List<Int>) =
        coordinateRepository.getCoordinatesByProjectIdsSync(projectIds)

    suspend fun getCoordinateByIdSync(id: Int) = coordinateRepository.getCoordinateByIdSync(id)

    fun createCoordinate(coordinate: Coordinate) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    coordinateRepository.createSync(coordinate)
                } catch (e: SQLiteConstraintException) {
                    _insertErrorMessage.postValue("此點位名稱已存在")
                }
            }
        }
    }

    fun createAllCoordinates(coordinates: List<Coordinate>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    coordinateRepository.createAllSync(coordinates)
                } catch (e: SQLiteConstraintException) {
                    _insertErrorMessage.postValue("含有重複點位名稱")
                }
            }
        }
    }

    fun updateCoordinate(coordinate: Coordinate) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    coordinateRepository.updateSync(coordinate)
                } catch (e: SQLiteConstraintException) {
                    _insertErrorMessage.postValue("此點位名稱已存在")
                }
            }
        }
    }

    fun deleteCoordinate(coordinate: Coordinate) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                coordinateRepository.deleteSync(coordinate)
            }
        }
    }

    fun refreshProjectCoordinates(projectId: Int, coordinates: List<Coordinate>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                coordinateRepository.deleteByProjectIdSync(projectId)
                coordinateRepository.createAllSync(coordinates)
            }
        }
    }

    private val _exportErrorMessage = MutableLiveData<String>()
    val exportErrorMessage: LiveData<String> = _exportErrorMessage

    private val _exportPath = MutableLiveData<String>()
    val exportPath: LiveData<String> = _exportPath

    fun saveCoordinatesToFile(project: Project, type: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val coordinates = coordinateRepository.getCoordinatesByProjectIdSync(project.id)

                var dataString = ""
                val dataBuilder = StringBuilder()

                when (type) {
                    "ctl" -> {
                        for (coordinate in coordinates) {
                            dataBuilder.append(coordinate.name).append("  ")
                                .append(coordinate.N).append(" ")
                                .append(coordinate.E).append("\r\n")
                        }
                        dataString = dataBuilder.toString()
                    }
                    "txt" -> {
                        for (coordinate in coordinates) {
                            dataBuilder.append(coordinate.name).append(" ")
                                .append(coordinate.N).append(" ")
                                .append(coordinate.E).append("\r\n")
                        }
                        dataString = dataBuilder.toString()
                    }
                    "csv" -> {
                        for (coordinate in coordinates) {
                            dataBuilder.append(coordinate.name).append(", ")
                                .append(coordinate.N).append(", ")
                                .append(coordinate.E).append("\r\n")
                        }
                        dataString = dataBuilder.toString()
                    }
                    "json" -> {
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        dataString = gson.toJson(coordinates.asOutputModel())
                    }
                }

                if (dataString == "") {
                    _exportErrorMessage.postValue("無法輸出檔案")
                } else {
                    val path = FileUtil.writeFileToDownload(
                        "${project.name}.$type",
                        dataString
                    )
                    if (path != null) {
                        path.apply {
                            _exportPath.postValue(this)
                        }
                    } else {
                        _exportErrorMessage.postValue("無法輸出檔案")
                    }
                }
            }
        }
    }

    private val _calculateResult = MutableLiveData<JSONObject?>()
    val calculateResult: LiveData<JSONObject?> = _calculateResult
    fun setCalculateResult(result: JSONObject?) {
        _calculateResult.value = result
    }

    private val _saveResultErrorMessage = MutableLiveData<String>()
    val saveResultErrorMessage: LiveData<String> = _saveResultErrorMessage

    fun createCalculateResult(result: CalculateResult) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    calculateResultRepository.createSync(result)
                } catch (e: SQLiteConstraintException) {
                    _saveResultErrorMessage.postValue("此成果名稱已存在")
                }
            }
        }
    }

    fun getCalculateResultsByType(type: String) = calculateResultRepository.getResultsByType(type)

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CoordinateListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CoordinateListViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct CoordinateListViewModel")
        }
    }
}