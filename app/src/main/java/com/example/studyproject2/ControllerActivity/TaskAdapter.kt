package com.example.studyproject3.ControllerActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.studyproject3.BaseTasks.Task
import com.example.studyproject3.databinding.ItemTaskBinding
import com.example.studyproject3.R


class TaskAdapter (
    private var tasks: MutableList<Task>,
    private val onDelete: (Task) -> Unit,
    private val onEdit: (Task) -> Unit
    ) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){

    class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder{
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.tvTaskName.text = task.title

        holder.binding.btnMenu.setOnClickListener { view ->
            // Создаем выпадающее меню, привязанное к этой кнопке
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.task_context_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete -> {
                        onDelete(task) // Вызываем удаление (передается из MainActivity)
                        true
                    }
                    R.id.action_edit -> {
                        // Здесь можно вызвать диалог редактирования

                        onEdit(task)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount() = tasks.size

    fun updateData(newTasks: List<Task>){
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}