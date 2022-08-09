package com.mhy.landrestoration.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mhy.landrestoration.database.dao.CalculateResultDao
import com.mhy.landrestoration.database.dao.CoordinateDao
import com.mhy.landrestoration.database.dao.ProjectDao
import com.mhy.landrestoration.database.model.CalculateResult
import com.mhy.landrestoration.database.model.Coordinate
import com.mhy.landrestoration.database.model.Project

@Database(entities = [Project::class, Coordinate::class, CalculateResult::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao
    abstract fun coordinateDao(): CoordinateDao
    abstract fun calculateResultDao(): CalculateResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}