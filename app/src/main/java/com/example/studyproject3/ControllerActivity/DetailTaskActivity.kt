package com.example.studyproject3.ControllerActivity

import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyproject3.BaseTasks.SubTask
import com.example.studyproject3.BaseTasks.Task
import com.example.studyproject3.databinding.ActivityDetailTaskBinding
import com.example.studyproject3.R
import com.example.studyproject3.ControllerAdapter.SubTaskAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.koin.android.ext.android.inject

class DetailTaskActivity : AppCompatActivity() {
    private var _binding: ActivityDetailTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var subTaskAdapter: SubTaskAdapter
    private var taskId: String = ""
    private var currentTask: Task? = null
    
    private val firestore: FirebaseFirestore by inject()
    private var subTasksListener: ListenerRegistration? = null
    private var taskListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailTaskBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        taskId = intent.getStringExtra("TASK_ID") ?: ""
        
        setupRecyclerView()
        observeTaskInfo()
        observeSubTasks()

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
            val dialog = AddTaskDialog { title, deadline ->
                saveSubTask(title, deadline)
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

    private fun observeTaskInfo() {
        if (taskId.isEmpty()) return
        
        taskListener = firestore.collection("tasks").document(taskId)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                
                currentTask = snapshot.toObject(Task::class.java)?.apply { id = snapshot.id }
                currentTask?.let {
                    binding.tvTaskName.text = it.title
                    binding.tvDeadlineDetail.text = "До: ${it.deadline}"
                    binding.pbTaskDetail.progress = it.percent
                    binding.tvPercentDetail.text = "${it.percent}%"
                }
            }
    }

    private fun observeSubTasks() {
        if (taskId.isEmpty()) return

        subTasksListener = firestore.collection("subtasks")
            .whereEqualTo("taskId", taskId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Ошибка загрузки подзадач", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val subTasks = snapshots?.map { doc ->
                    doc.toObject(SubTask::class.java).apply { id = doc.id }
                } ?: emptyList()
                
                subTaskAdapter.updateData(subTasks)
                
                // Считаем прогресс на основе нового поля 'completed'
                val completedCount = subTasks.count { it.completed }
                val totalCount = subTasks.size
                val percent = if (totalCount == 0) 0 else (completedCount.toFloat() / totalCount * 100).toInt()
                
                updateUIAndCloudProgress(percent)
            }
    }

    private fun saveSubTask(title: String, deadline: String) {
        val newSubTask = SubTask(taskId = taskId, title = title, deadline = deadline)
        firestore.collection("subtasks").add(newSubTask)
    }

    private fun updateSubTask(subTask: SubTask) {
        if (subTask.id.isEmpty()) return
        firestore.collection("subtasks").document(subTask.id).set(subTask)
    }

    private fun deleteSubTask(subTask: SubTask) {
        if (subTask.id.isEmpty()) return
        firestore.collection("subtasks").document(subTask.id).delete()
    }

    private fun editSubTask(subTask: SubTask) {
        val dialog = AddTaskDialog(
            initialTitle = subTask.title,
            initialDeadline = subTask.deadline
        ) { newTitle, newDeadline ->
            updateSubTask(subTask.copy(title = newTitle, deadline = newDeadline))
        }
        dialog.show(supportFragmentManager, "EditSubTaskDialog")
    }

    private fun deleteMainTask() {
        firestore.collection("subtasks").whereEqualTo("taskId", taskId).get()
            .addOnSuccessListener { snapshots ->
                for (doc in snapshots) {
                    doc.reference.delete()
                }
                firestore.collection("tasks").document(taskId).delete()
                    .addOnSuccessListener { finish() }
            }
    }

    private fun editMainTask() {
        currentTask?.let { task ->
            val dialog = AddTaskDialog(
                initialTitle = task.title,
                initialDeadline = task.deadline
            ) { newTitle, newDeadline ->
                val updatedData = mapOf(
                    "title" to newTitle,
                    "deadline" to newDeadline
                )
                firestore.collection("tasks").document(taskId).update(updatedData)
            }
            dialog.show(supportFragmentManager, "EditMainTaskDialog")
        }
    }

    private fun updateUIAndCloudProgress(percent: Int) {
        // Мгновенно обновляем полоску на экране
        binding.pbTaskDetail.progress = percent
        binding.tvPercentDetail.text = "$percent%"
        
        // Отправляем в облако только если значение изменилось
        if (currentTask?.percent != percent) {
            firestore.collection("tasks").document(taskId).update("percent", percent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subTasksListener?.remove()
        taskListener?.remove()
        _binding = null
    }
}