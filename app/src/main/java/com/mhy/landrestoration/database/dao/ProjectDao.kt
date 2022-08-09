package com.mhy.landrestoration.database.dao

import androidx.room.*
import com.mhy.landrestoration.database.model.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM project ORDER BY name ASC")
    fun getProjects(): Flow<List<Project>>

    @Query("SELECT * FROM project ORDER BY name ASC")
    suspend fun getProjectsSync(): List<Project>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSync(project: Project)

    @Update
    suspend fun updateSync(project: Project)

    @Delete
    suspend fun deleteSync(project: Project)
}