package com.mhy.landrestoration.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mhy.landrestoration.database.coordinate.Coordinate
import com.mhy.landrestoration.database.coordinate.Project

class CoordinateSelectViewModel : ViewModel() {

    private val _selectedProjects = MutableLiveData<List<Project>>()
    val selectedProjects: LiveData<List<Project>> = _selectedProjects
    fun setSelectedProjects(projects: List<Project>) {
        _selectedProjects.value = projects
    }

    private val _selectedCoordinate = MutableLiveData<Coordinate>()
    val selectCoordinate: LiveData<Coordinate> = _selectedCoordinate
    fun setSelectedCoordinate(coordinate: Coordinate) {
        _selectedCoordinate.value = coordinate
    }
}