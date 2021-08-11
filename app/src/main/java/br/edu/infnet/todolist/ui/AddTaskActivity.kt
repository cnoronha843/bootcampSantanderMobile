package br.edu.infnet.todolist.ui

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.infnet.todolist.databinding.ActivityAddTaskBinding
import br.edu.infnet.todolist.datasource.TaskDataSource
import br.edu.infnet.todolist.extensions.format
import br.edu.infnet.todolist.extensions.text
import br.edu.infnet.todolist.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class AddTaskActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)){
            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(taskId)?.let {
                binding.tilTitle.text = it.title
                binding.tilTime.text = it.hour
                binding.tilDate.text = it.date
                binding.tilDescription.text = it.description
            }
        }

        insertListeners()
    }

    private fun insertListeners() {
        binding.tilDate.editText?.setOnClickListener{
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) *-1
                binding.tilDate.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")

        }

        binding.tilTime.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()
            timePicker.addOnPositiveButtonClickListener{
               val minute = if (timePicker.minute in 0..9) "0${timePicker.minute}" else timePicker.minute
               val hour = if (timePicker.hour in 0..9) "0${timePicker.hour}" else timePicker.hour
                binding.tilTime.text = "$hour:$minute"

            }
            timePicker.show(supportFragmentManager,null)
        }
        binding.btnCancel.setOnClickListener {
            finish()
        }
        binding.btnNewTask.setOnClickListener {
            val task = Task(
                 title = binding.tilTitle.text ,
                 description = binding.tilDescription.text,
                 hour = binding.tilDate.text,
                 date = binding.tilTime.text,
                id = intent.getIntExtra(TASK_ID, 0)
            )

            TaskDataSource.insertTask(task)
            setResult(Activity.RESULT_OK)
            finish()
        }

    }
    companion object {
        const val  TASK_ID = "task_id"
    }
}