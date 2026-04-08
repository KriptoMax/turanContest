package com.example.studyproject3.BaseTasks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subtasks")
data class SubTask(
    @PrimaryKey
    var id: String = "",
    val taskId: String = "",
    val title: String = "",
    val deadline: String = "",
    val completed: Boolean = false // Переименовано для корректной работы с Firebase
)