package com.mhy.landrestoration.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.mhy.landrestoration.database.AppDatabase
import com.mhy.landrestoration.database.coordinate.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProjectRepository(private val database: AppDatabase) {

    val projects: LiveData<List<Project>> = database.projectDao().getProjects().asLiveData()

    suspend fun create(project: Project) = database.projectDao().insert(project)

    suspend fun delete(project: Project) = database.projectDao().delete(project)
}