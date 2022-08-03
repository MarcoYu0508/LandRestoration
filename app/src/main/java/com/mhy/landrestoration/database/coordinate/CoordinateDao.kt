package com.mhy.landrestoration.database.coordinate

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CoordinateDao {

    @Query("SELECT * FROM coordinate WHERE project_id = :projectId ORDER BY name ASC")
    fun getByProject(projectId: Int): Flow<List<Coordinate>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(coordinate: Coordinate)

    @Update
    suspend fun update(coordinate: Coordinate)

    @Delete
    suspend fun delete(coordinate: Coordinate)
}