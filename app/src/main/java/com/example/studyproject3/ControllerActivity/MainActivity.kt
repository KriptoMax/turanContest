package com.example.studyproject3.ControllerActivity

import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyproject3.BaseTasks.Task
import com.example.studyproject3.HandlerController.TransitionController
import com.example.studyproject3.databinding.ActivityMainBinding
import com.example.studyproject3.R
import com.example.studyproject3.ControllerAdapter.TaskAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskAdapter: TaskAdapter
    private val firestore: FirebaseFirestore by inject()
    private var tasksListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val controller = TransitionController(binding, this)
        controller.oneTask()
        
        setupRecyclerView()
        observeTasks()

        binding.btn1Create.setOnClickListener {
            val dialog = AddTaskDialog { title, deadline ->
                saveTask(title, deadline)
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
            onDelete = { task -> deleteTask(task) },
            onEdit = { task ->
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

    private fun observeTasks() {
        tasksListener = firestore.collection("tasks")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Ошибка загрузки: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val taskList = snapshots?.map { doc ->
                    doc.toObject(Task::class.java).apply { id = doc.id }
                } ?: emptyList()
                
                taskAdapter.updateData(taskList)
            }
    }

    private fun saveTask(title: String, deadline: String) {
        val newTask = Task(title = title, deadline = deadline)
        firestore.collection("tasks").add(newTask)
            .addOnSuccessListener {
                Toast.makeText(this, "Задача добавлена в облако", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateTask(task: Task, newTitle: String, newDeadline: String) {
        val updatedData = mapOf(
            "title" to newTitle,
            "deadline" to newDeadline
        )
        firestore.collection("tasks").document(task.id).update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(this, "Обновлено", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteTask(task: Task) {
        firestore.collection("tasks").document(task.id).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Удалено", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        tasksListener?.remove()
        _binding = null
    }
}