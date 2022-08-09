package com.mhy.landrestoration.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.mhy.landrestoration.database.AppDatabase
import com.mhy.landrestoration.database.model.CalculateResult

class CalculateResultRepository(private val database: AppDatabase) {

    fun getResultsByType(type: String): LiveData<List<CalculateResult>> {
        return database.calculateResultDao().getResultsByType(type).asLiveData()
    }

    suspend fun createSync(result: CalculateResult) =
        database.calculateResultDao().insertSync(result)
}