package com.mhy.landrestoration.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.mhy.landrestoration.database.AppDatabase
import com.mhy.landrestoration.database.coordinate.Coordinate

class CoordinateRepository(private val database: AppDatabase) {

    fun getCoordinatesByProjectId(projectId: Int): LiveData<List<Coordinate>> {
        return database.coordinateDao().getByProject(projectId).asLiveData()
    }

    suspend fun getCoordinatesByProjectIdSync(projectId: Int) =
        database.coordinateDao().getByProjectSync(projectId)

    suspend fun getCoordinatesByProjectIdsSync(projectIds: List<Int>) =
        database.coordinateDao().getByProjectsSync(projectIds)

    suspend fun create(coordinate: Coordinate) = database.coordinateDao().insert(coordinate)

    suspend fun createAll(coordinates: List<Coordinate>) =
        database.coordinateDao().insertAll(coordinates)

    suspend fun update(coordinate: Coordinate) = database.coordinateDao().update(coordinate)

    suspend fun delete(coordinate: Coordinate) = database.coordinateDao().delete(coordinate)

    suspend fun deleteByProjectId(projectId: Int) =
        database.coordinateDao().deleteByProject(projectId)
}