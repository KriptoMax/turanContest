package com.example.studyproject3.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.studyproject3.BaseTasks.SubTask

@Dao
interface SubTaskDao {

    @Insert
    suspend fun insertSubTask(subTask: SubTask)

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId ORDER BY id DESC")
    suspend fun getSubTasksByTaskId(taskId: Int): List<SubTask>

    @Delete
    suspend fun deleteSubTask(subTask: SubTask)

    @Update
    suspend fun updateSubTask(subTask: SubTask)
}