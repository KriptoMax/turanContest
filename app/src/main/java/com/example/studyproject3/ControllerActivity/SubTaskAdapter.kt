package com.example.studyproject3.ControllerActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.studyproject3.BaseTasks.SubTask
import com.example.studyproject3.databinding.ItemSubTaskBinding
import com.example.studyproject3.R

class SubTaskAdapter(
    private var subTasks: MutableList<SubTask>,
    private val onToggle: (SubTask) -> Unit,
    private val onDelete: (SubTask) -> Unit,
    private val onEdit: (SubTask) -> Unit
) : RecyclerView.Adapter<SubTaskAdapter.SubTaskViewHolder>() {

    class SubTaskViewHolder(val binding: ItemSubTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder {
        val binding = ItemSubTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubTaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int) {
        val subTask = subTasks[position]
        holder.binding.tvSubTaskTitle.text = subTask.title
        holder.binding.tvSubTaskDeadline.text = if (subTask.deadline.isNotEmpty()) "До: ${subTask.deadline}" else ""
        
        holder.binding.cbSubTask.setOnCheckedChangeListener(null)
        holder.binding.cbSubTask.isChecked = subTask.isCompleted

        holder.binding.cbSubTask.setOnCheckedChangeListener { _, isChecked ->
            onToggle(subTask.copy(isCompleted = isChecked))
        }

        holder.binding.btnMenuSub.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.subtask_context_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete_sub -> { onDelete(subTask); true }
                    R.id.action_edit_sub -> { onEdit(subTask); true }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount() = subTasks.size

    fun updateData(newSubTasks: List<SubTask>) {
        subTasks.clear()
        subTasks.addAll(newSubTasks)
        notifyDataSetChanged()
    }
}