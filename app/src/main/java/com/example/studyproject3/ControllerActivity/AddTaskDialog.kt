package com.example.studyproject3.ControllerActivity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.studyproject3.databinding.DialogAddTaskBinding

class AddTaskDialog(
    private val initialTitle: String = "",
    private val initialDeadline: String = "",
    private val onTaskAdded: (String, String) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddTaskBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for DialogAddTaskBinding must not be null")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddTaskBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Предзаполняем поля данными, если они переданы
        binding.etTaskName.setText(initialTitle)
        binding.etDeadline.setText(initialDeadline)

        // Если это редактирование, меняем заголовок диалога
        if (initialTitle.isNotEmpty()) {
            binding.tvTitle.text = "Редактирование"
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTaskName.text.toString()
            val deadline = binding.etDeadline.text.toString()
            if (title.isNotEmpty()) {
                onTaskAdded(title, deadline)
                dismiss()
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}