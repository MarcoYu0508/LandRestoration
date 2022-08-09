package com.mhy.landrestoration.viewmodels

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.*
import com.mhy.landrestoration.database.AppDatabase
import com.mhy.landrestoration.database.model.CalculateResult
import com.mhy.landrestoration.database.model.Coordinate
import com.mhy.landrestoration.database.model.Project
import com.mhy.landrestoration.model.SelectedPointItem
import com.mhy.landrestoration.repository.CalculateResultRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class CoordinateSelectViewModel : ViewModel() {

    private val _selectedProjects = MutableLiveData<List<Project>>()
    val selectedProjects: LiveData<List<Project>> = _selectedProjects
    fun setSelectedProjects(projects: List<Project>) {
        _selectedProjects.value = projects
    }

    private val _selectedIndex = MutableLiveData<Int>()
    fun setSelectedIndex(index: Int) {
        _selectedIndex.value = index
    }

    fun setSelectedCoordinate(coordinate: Coordinate) {
        _selectedIndex.value?.let { index ->
            val item = _selectedPointItems.value?.get(index)
            item?.apply {
                name = coordinate.name
                N = coordinate.N
                E = coordinate.E
            }
        }
    }

    private val _selectedPointItems = MutableLiveData<List<SelectedPointItem>>(listOf())
    val selectedPointItems: LiveData<List<SelectedPointItem>> = _selectedPointItems
    fun setSelectedPointItems(items: List<SelectedPointItem>) {
        _selectedPointItems.value = items
    }
}