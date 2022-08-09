package com.mhy.landrestoration.database.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["type", "name"], unique = true)])
data class CalculateResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @NonNull val type: String,
    @NonNull val name: String,
    @NonNull val result: String,
)