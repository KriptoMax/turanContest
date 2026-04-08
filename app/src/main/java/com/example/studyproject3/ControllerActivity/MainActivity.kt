package com.example.studyproject3.ControllerActivity

import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.studyproject3.BaseTasks.Task
import com.example.studyproject3.data.RoomDatabase.AppDatabase
import com.example.studyproject3.HandlerController.TransitionController
import com.example.studyproject3.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import com.example.studyproject3.R


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for MainActivity must not be null")

    private lateinit var taskAdapter: TaskAdapter

    val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "my_app_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val controller = TransitionController(binding, this)
        controller.oneTask()
        setupRecyclerView()
        loadTasks()

        binding.btn1Create.setOnClickListener {
            // Для новой задачи передаем пустые строки
            val dialog = AddTaskDialog { title, deadline ->
                saveTask(title, deadline)
                Toast.makeText(this, "Добавлено: $title", Toast.LENGTH_SHORT).show()
            }
            dialog.show(supportFragmentManager, "AddTaskDialog")
        }

        binding.btnProfile.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.top_app_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.creator_nickname) {
                    Toast.makeText(this, "Это ваш профиль", Toast.LENGTH_SHORT).show()
                    true
                } else false
            }
            popup.show()
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            mutableListOf(),
            onDelete = { task ->
                lifecycleScope.launch {
                    db.taskDao().deleteTask(task)
                    loadTasks()
                }
            },
            onEdit = { task ->
                // Передаем текущие данные задачи для редактирования
                val dialog = AddTaskDialog(
                    initialTitle = task.title,
                    initialDeadline = task.deadline
                ) { updatedTitle, updatedDeadline ->
                    updateTask(task, updatedTitle, updatedDeadline)
                }
                dialog.show(supportFragmentManager, "EditTaskDialog")
            }
        )
        binding.recV.layoutManager = LinearLayoutManager(this)
        binding.recV.adapter = taskAdapter
    }

    private fun loadTasks() {
        lifecycleScope.launch {
            val tasks = db.taskDao().getAllTasks()
            taskAdapter.updateData(tasks)
        }
    }

    private fun saveTask(title: String, deadline: String) {
        lifecycleScope.launch {
            val newTask = Task(title = title, deadline = deadline)
            db.taskDao().insertTask(newTask)
            loadTasks()
        }
    }

    private fun updateTask(task: Task, newTitle: String, newDeadline: String) {
        lifecycleScope.launch {
            val updatedTask = task.copy(title = newTitle, deadline = newDeadline)
            db.taskDao().updateTask(updatedTask)
            loadTasks()
            Toast.makeText(this@MainActivity, "Задача обновлена", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }
}