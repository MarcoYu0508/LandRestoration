package com.mhy.landrestoration.database.coordinate

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM project ORDER BY name ASC")
    fun getProjects(): Flow<List<Project>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(project: Project)

    @Update
    suspend fun update(project: Project)

    @Delete
    suspend fun delete(project: Project)
}