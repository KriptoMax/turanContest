package com.example.studyproject3.ControllerActivity

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
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
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.tvTaskName.text = task.title
        holder.binding.tvDeadline.text = "До: ${task.deadline}"
        holder.binding.pbTask.progress = task.percent
        holder.binding.tvPercent.text = "${task.percent}%"

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailTaskActivity::class.java)
            intent.putExtra("TASK_ID", task.id)
            intent.putExtra("TASK_TITLE", task.title)
            holder.itemView.context.startActivity(intent)
        }

        holder.binding.btnMenu.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.task_context_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete -> { onDelete(task); true }
                    R.id.action_edit -> { onEdit(task); true }
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