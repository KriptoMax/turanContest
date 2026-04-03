package com.example.studyproject3.ControllerActivity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.studyproject3.databinding.DialogAddTaskBinding


class AddTaskDialog(val onTaskAdded: (String) -> Unit) : DialogFragment(){

    private var _binding: DialogAddTaskBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for DialogAddTaskBinding must not be null") //ActivityLearnWordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddTaskBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.btnSave.setOnClickListener {
            val text = binding.etTaskName.text.toString()
            if (text.isNotEmpty()) {
                onTaskAdded(text)
                dismiss()
            }
        }
        return binding.root
    }
    // Это чтобы окно было красивым и на всю ширину
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}