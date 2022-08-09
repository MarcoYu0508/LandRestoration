package com.mhy.landrestoration.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.mhy.landrestoration.database.AppDatabase
import com.mhy.landrestoration.database.model.Coordinate

class CoordinateRepository(private val database: AppDatabase) {

    fun getCoordinatesByProjectId(projectId: Int): LiveData<List<Coordinate>> {
        return database.coordinateDao().getByProject(projectId).asLiveData()
    }

    suspend fun getCoordinatesByProjectIdSync(projectId: Int) =
        database.coordinateDao().getByProjectSync(projectId)

    suspend fun getCoordinatesByProjectIdsSync(projectIds: List<Int>) =
        database.coordinateDao().getByProjectsSync(projectIds)

    suspend fun getCoordinateByIdSync(id: Int) = database.coordinateDao().getByIdSync(id)

    suspend fun createSync(coordinate: Coordinate) = database.coordinateDao().insertSync(coordinate)

    suspend fun createAllSync(coordinates: List<Coordinate>) =
        database.coordinateDao().insertAllSync(coordinates)

    suspend fun updateSync(coordinate: Coordinate) = database.coordinateDao().updateSync(coordinate)

    suspend fun deleteSync(coordinate: Coordinate) = database.coordinateDao().deleteSync(coordinate)

    suspend fun deleteByProjectIdSync(projectId: Int) =
        database.coordinateDao().deleteByProjectSync(projectId)
}