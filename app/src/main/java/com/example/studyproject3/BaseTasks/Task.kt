package com.example.studyproject3.BaseTasks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val deadline: String = "",
    val percent: Int = 0,
)