package com.mhy.landrestoration.database.dao

import androidx.room.*
import com.mhy.landrestoration.database.model.Coordinate
import kotlinx.coroutines.flow.Flow

@Dao
interface CoordinateDao {

    @Query("SELECT * FROM coordinate WHERE project_id = :projectId ORDER BY name ASC")
    fun getByProject(projectId: Int): Flow<List<Coordinate>>

    @Query("SELECT * FROM coordinate WHERE project_id = :projectId ORDER BY name ASC")
    suspend fun getByProjectSync(projectId: Int): List<Coordinate>

    @Query("SELECT * FROM coordinate WHERE project_id IN (:projectIds) ORDER BY name ASC")
    suspend fun getByProjectsSync(projectIds: List<Int>): List<Coordinate>

    @Query("SELECT * FROM coordinate WHERE id = :id")
    suspend fun getByIdSync(id: Int): Coordinate

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSync(coordinate: Coordinate)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllSync(coordinates: List<Coordinate>)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateSync(coordinate: Coordinate)

    @Delete
    suspend fun deleteSync(coordinate: Coordinate)

    @Query("DELETE FROM coordinate WHERE project_id = :projectId ")
    suspend fun deleteByProjectSync(projectId: Int)

    @Delete
    suspend fun deleteAllSync(coordinates: List<Coordinate>)
}