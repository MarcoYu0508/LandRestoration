package com.mhy.landrestoration.database.coordinate

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"], unique = true)])
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @NonNull val name: String
)