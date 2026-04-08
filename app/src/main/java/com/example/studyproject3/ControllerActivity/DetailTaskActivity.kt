package com.example.studyproject3.ControllerActivity

import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.studyproject3.BaseTasks.SubTask
import com.example.studyproject3.BaseTasks.Task
import com.example.studyproject3.data.RoomDatabase.AppDatabase
import com.example.studyproject3.databinding.ActivityDetailTaskBinding
import kotlinx.coroutines.launch
import com.example.studyproject3.R

class DetailTaskActivity : AppCompatActivity() {
    private var _binding: ActivityDetailTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var subTaskAdapter: SubTaskAdapter
    private var taskId: Int = -1
    private var currentTask: Task? = null

    val db by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "my_app_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailTaskBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        taskId = intent.getIntExtra("TASK_ID", -1)
        
        setupRecyclerView()
        loadTaskInfo()
        loadSubTasks()

        binding.tvTaskVault.setOnClickListener {
            finish()
        }

        binding.btnMenu.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.task_context_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete -> { deleteMainTask(); true }
                    R.id.action_edit -> { editMainTask(); true }
                    else -> false
                }
            }
            popup.show()
        }

        binding.btn1Create1.setOnClickListener {
            val dialog = AddTaskDialog { title, _ ->
                saveSubTask(title)
            }
            dialog.show(supportFragmentManager, "AddSubTaskDialog")
        }
    }

    private fun setupRecyclerView() {
        subTaskAdapter = SubTaskAdapter(
            mutableListOf(),
            onToggle = { subTask -> updateSubTask(subTask) },
            onDelete = { subTask -> deleteSubTask(subTask) },
            onEdit = { subTask -> editSubTask(subTask) }
        )
        binding.recV.layoutManager = LinearLayoutManager(this)
        binding.recV.adapter = subTaskAdapter
    }

    private fun loadTaskInfo() {
        lifecycleScope.launch {
            currentTask = db.taskDao().getAllTasks().find { it.id == taskId }
            currentTask?.let {
                binding.tvTaskName.text = it.title
                binding.tvDeadlineDetail.text = "До: ${it.deadline}"
                binding.pbTaskDetail.progress = it.percent
                binding.tvPercentDetail.text = "${it.percent}%"
            }
        }
    }

    private fun loadSubTasks() {
        lifecycleScope.launch {
            val subTasks = db.subTaskDao().getSubTasksByTaskId(taskId)
            subTaskAdapter.updateData(subTasks)
            calculateAndSaveProgress(subTasks)
        }
    }

    private fun saveSubTask(title: String) {
        lifecycleScope.launch {
            val newSubTask = SubTask(taskId = taskId, title = title)
            db.subTaskDao().insertSubTask(newSubTask)
            loadSubTasks()
        }
    }

    private fun updateSubTask(subTask: SubTask) {
        lifecycleScope.launch {
            db.subTaskDao().updateSubTask(subTask)
            loadSubTasks()
        }
    }

    private fun deleteSubTask(subTask: SubTask) {
        lifecycleScope.launch {
            db.subTaskDao().deleteSubTask(subTask)
            loadSubTasks()
        }
    }

    private fun editSubTask(subTask: SubTask) {
        // ПРЕДЗАПОЛНЕНИЕ для подзадачи
        val dialog = AddTaskDialog(initialTitle = subTask.title) { newTitle, _ ->
            lifecycleScope.launch {
                db.subTaskDao().updateSubTask(subTask.copy(title = newTitle))
                loadSubTasks()
            }
        }
        dialog.show(supportFragmentManager, "EditSubTaskDialog")
    }

    private fun deleteMainTask() {
        lifecycleScope.launch {
            currentTask?.let {
                db.taskDao().deleteTask(it)
                finish()
            }
        }
    }

    private fun editMainTask() {
        // ПРЕДЗАПОЛНЕНИЕ для основной задачи
        currentTask?.let { task ->
            val dialog = AddTaskDialog(
                initialTitle = task.title,
                initialDeadline = task.deadline
            ) { newTitle, newDeadline ->
                lifecycleScope.launch {
                    val updated = task.copy(title = newTitle, deadline = newDeadline)
                    db.taskDao().updateTask(updated)
                    loadTaskInfo()
                }
            }
            dialog.show(supportFragmentManager, "EditMainTaskDialog")
        }
    }

    private suspend fun calculateAndSaveProgress(subTasks: List<SubTask>) {
        val percent = if (subTasks.isEmpty()) 0 else {
            (subTasks.count { it.isCompleted }.toFloat() / subTasks.size * 100).toInt()
        }
        
        currentTask?.let {
            val updatedTask = it.copy(percent = percent)
            db.taskDao().updateTask(updatedTask)
            binding.pbTaskDetail.progress = percent
            binding.tvPercentDetail.text = "$percent%"
            currentTask = updatedTask
        }
    }
}