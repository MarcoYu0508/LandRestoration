package com.mhy.landrestoration.database.coordinate

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CoordinateDao {

    @Query("SELECT * FROM coordinate WHERE project_id = :projectId ORDER BY name ASC")
    fun getByProject(projectId: Int): Flow<List<Coordinate>>

    @Query("SELECT * FROM coordinate WHERE project_id = :projectId ORDER BY name ASC")
    suspend fun getByProjectSync(projectId: Int): List<Coordinate>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(coordinate: Coordinate)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(coordinates: List<Coordinate>)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(coordinate: Coordinate)

    @Delete
    suspend fun delete(coordinate: Coordinate)

    @Query("DELETE FROM coordinate WHERE project_id = :projectId ")
    suspend fun deleteByProject(projectId: Int)

    @Delete
    suspend fun deleteAll(coordinates: List<Coordinate>)
}