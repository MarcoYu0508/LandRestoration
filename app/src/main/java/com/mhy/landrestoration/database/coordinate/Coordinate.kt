package com.mhy.landrestoration.database.coordinate

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["project_id", "name"], unique = true)])
data class Coordinate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @NonNull val project_id: Int,
    @NonNull val name: String,
    @NonNull val E: Double,
    @NonNull val N: Double
)