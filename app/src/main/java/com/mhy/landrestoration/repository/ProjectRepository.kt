package com.mhy.landrestoration.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.mhy.landrestoration.database.AppDatabase
import com.mhy.landrestoration.database.model.Project

class ProjectRepository(private val database: AppDatabase) {

    val projects: LiveData<List<Project>> = database.projectDao().getProjects().asLiveData()

    suspend fun getProjectsSync() = database.projectDao().getProjectsSync()

    suspend fun createSync(project: Project) = database.projectDao().insertSync(project)

    suspend fun deleteSync(project: Project) = database.projectDao().deleteSync(project)
}