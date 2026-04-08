package com.example.studyproject3.BaseTasks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subtasks")
data class SubTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskId: Int,
    val title: String = "",
    val deadline: String = "",
    val isCompleted: Boolean = false
)