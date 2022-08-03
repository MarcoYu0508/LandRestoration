package com.mhy.landrestoration.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mhy.landrestoration.database.AppDatabase
import com.mhy.landrestoration.database.coordinate.Project
import com.mhy.landrestoration.repository.CoordinateRepository
import com.mhy.landrestoration.repository.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "CoordinateListViewModel"

class CoordinateListViewModel(application: Application) : AndroidViewModel(application) {
    private val projectRepository = ProjectRepository(AppDatabase.getDatabase(application))
    private val coordinateRepository = CoordinateRepository(AppDatabase.getDatabase(application))

    val projects = projectRepository.projects

    private val _insertErrorMessage = MutableLiveData<String>()
    val insertErrorMessage: LiveData<String> = _insertErrorMessage

    fun createProject(name: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    projectRepository.create(Project(name = name))
                } catch (e: Exception) {
                    _insertErrorMessage.postValue("此專案已存在")
                }
            }
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                projectRepository.delete(project)
            }
        }
    }

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