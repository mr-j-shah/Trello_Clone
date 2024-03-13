package com.crestinfosystems_jinay.trello.Screens_Activity.TaskDetails

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crestinfosystems_jinay.trello.data.Board
import com.crestinfosystems_jinay.trello.data.State
import com.crestinfosystems_jinay.trello.data.Task
import com.crestinfosystems_jinay.trello.databinding.ActivityTaskDetailsBinding
import com.crestinfosystems_jinay.trello.network.deleteNewTask
import com.crestinfosystems_jinay.trello.network.updateNewTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class TaskDetails : AppCompatActivity() {
    var binding: ActivityTaskDetailsBinding? = null
    var taskFromIntent: Task? = null
    var baordFromIntent: Board? = null
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        taskFromIntent = intent.getParcelableExtra("task") ?: Task()
        baordFromIntent = intent.getParcelableExtra("board") ?: Board()
        setDataOnInit(task = taskFromIntent!!)
        setSupportActionBar(binding?.toolbarExercise)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }
        binding?.lastEditBy?.text = taskFromIntent?.lastEdit
        buttonFunctionality()
        setContentView(binding?.root)
    }

    private fun buttonFunctionality() {
        if (taskFromIntent?.state == State.done) {
            binding?.cardDone?.visibility = View.GONE
            binding?.cardPin?.visibility = View.GONE
            binding?.cardTodo?.visibility = View.VISIBLE
        } else if (taskFromIntent?.state == State.doing) {
            binding?.cardDone?.visibility = View.VISIBLE
            binding?.cardPin?.visibility = View.GONE
            binding?.cardTodo?.visibility = View.VISIBLE
        }
        binding?.cardTodo?.setOnClickListener {
            taskFromIntent?.copy(state = State.todo)
            updateStatus(state = State.todo, board = baordFromIntent!!) {
                onBackPressed()
                Toast.makeText(this, "Make this TODO", Toast.LENGTH_SHORT).show()
            }
        }
        binding?.cardDone?.setOnClickListener {
            taskFromIntent?.copy(state = State.done)
            updateStatus(state = State.done, board = baordFromIntent!!) {
                onBackPressed()
                Toast.makeText(this, "State Updates", Toast.LENGTH_SHORT).show()
            }
        }
        binding?.cardPin?.setOnClickListener {
            updateStatus(state = State.doing, board = baordFromIntent!!) {
                onBackPressed()
                taskFromIntent?.copy(state = State.doing)
                Toast.makeText(this, "State Updates", Toast.LENGTH_SHORT).show()
            }
        }
        binding?.cardDelete?.setOnClickListener {
            deleteNewTask(
                taskFromIntent!!,
                board = baordFromIntent!!,
                context = applicationContext
            ) {
                onBackPressed()
                Toast.makeText(this, "Task Deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStatus(state: State, board: Board, onTap: () -> Unit) {
        updateNewTask(
            taskFromIntent!!.copy(
                state = state,
                lastEdit = FirebaseAuth.getInstance().currentUser?.email.toString()
            ), board, context = applicationContext
        ) {
            onTap()
        }
    }

    private fun setDataOnInit(task: Task) {
        binding?.taskTitle?.text = task.title
        binding?.taskDesc?.text = task.desc
    }
}