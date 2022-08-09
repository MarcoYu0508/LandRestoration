package com.mhy.landrestoration.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mhy.landrestoration.database.model.CalculateResult
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculateResultDao {

    @Query("SELECT * FROM calculateresult WHERE type = :type ORDER BY name ASC")
    fun getResultsByType(type: String): Flow<List<CalculateResult>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSync(result: CalculateResult)
}