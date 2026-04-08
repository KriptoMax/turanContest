package com.example.studyproject3.BaseTasks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey
    var id: String = "", // Firebase uses String IDs
    val title: String = "",
    val deadline: String = "",
    val percent: Int = 0,
)