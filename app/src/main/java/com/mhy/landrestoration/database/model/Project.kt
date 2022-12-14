package com.mhy.landrestoration.database.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(indices = [Index(value = ["name"], unique = true)])
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @NonNull val name: String
) : Serializable