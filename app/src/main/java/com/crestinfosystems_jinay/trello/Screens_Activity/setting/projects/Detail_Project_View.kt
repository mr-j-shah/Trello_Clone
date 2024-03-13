package com.crestinfosystems_jinay.trello.Screens_Activity.setting.projects

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crestinfosystems_jinay.trello.adapter.AutoCompleteViewAd
import com.crestinfosystems_jinay.trello.adapter.TaskAdapter
import com.crestinfosystems_jinay.trello.data.Board
import com.crestinfosystems_jinay.trello.data.State
import com.crestinfosystems_jinay.trello.data.Task
import com.crestinfosystems_jinay.trello.databinding.ActivityDetailProjectViewBinding
import com.crestinfosystems_jinay.trello.databinding.AddTaskBottomSheetBinding
import com.crestinfosystems_jinay.trello.databinding.EditAssigneesBinding
import com.crestinfosystems_jinay.trello.network.addNewTask
import com.crestinfosystems_jinay.trello.network.readUserAllUserOnApplication
import com.crestinfosystems_jinay.trello.network.updateNewBoard
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Detail_Project_View : AppCompatActivity() {
    var list = mutableListOf<Task>()
    var binding: ActivityDetailProjectViewBinding? = null
    var baoardAdapter: TaskAdapter? = null
    var assignTo: MutableList<String> = mutableListOf<String>()
    var suggestions: List<String> = listOf()
    var customAdapter: AutoCompleteViewAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailProjectViewBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val board: Board = intent.getParcelableExtra("board") ?: Board(name = "Trello")
        if (board.createdBy != FirebaseAuth.getInstance().currentUser?.email) {
            binding?.editProject?.visibility = View.GONE
        }
        baoardAdapter = TaskAdapter(
            arrayListOf(), this, board
        )
        binding?.toolbarExercise?.title = board.name
        setContentView(binding?.root)
        realTimeDataChange(board)
        setSupportActionBar(binding?.toolbarExercise)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }
        binding?.floatingActionButton?.setOnClickListener {
            Dialogfunction(board)
        }
        binding?.editProject?.setOnClickListener {
            editDialogFunction(board)
        }
        setAdpater()
    }

    private fun setAdpater() {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.recyclerViewAdapter?.layoutManager = layoutManager
        binding?.recyclerViewAdapter?.adapter = baoardAdapter
    }

    private fun realTimeDataChange(board: Board) {
        FirebaseDatabase.getInstance().reference.child("Projects").child(board.name)
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    val value = snapshot.getValue(false) as Map<*, *>
                    if (value["task"] != null) {
                        val taskList = value["task"] as Map<*, *>
                        for ((key, value) in taskList) {
                            val valueOfTask = (value as Map<String, Any>).toMutableMap()
                            valueOfTask["key"] = key as Any
                            list.add(Task(valueOfTask))
                        }
                    }
                    baoardAdapter?.submitList(list as ArrayList<Task>)
                    println("Data from Firebase: $value")
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                    println("Error reading data: ${error.message}")
                }
            })
    }

    private fun editDialogFunction(board: Board) {
        var user = com.google.firebase.ktx.Firebase.auth.currentUser
        val editDialog = EditAssigneesBinding.inflate(layoutInflater)
        CoroutineScope(Dispatchers.IO).launch {
            var data = readUserAllUserOnApplication()
            Log.d("User Data", data.toString())
            withContext(Dispatchers.Main) {
                editDialog.editDialogContent.visibility = View.VISIBLE
                if (data != null) {
                    suggestions = data
                }
                suggestions -= user?.email!!
                suggestions -= board.assignedTo
                Log.d("Assign List", "editDialogFunction: $suggestions")
                for (i in board.assignedTo) {
                    addChip(i, editDialog)
                }
                val customAdapter = this@Detail_Project_View.let {
                    AutoCompleteViewAd(it, suggestions as ArrayList<String>) { selectedSuggestion ->
                        Toast.makeText(it, "User: $selectedSuggestion", Toast.LENGTH_SHORT).show()
                        if (!assignTo.contains(selectedSuggestion)) {
                            addChip(selectedSuggestion, editDialog)
                        }
                        editDialog.autoCompleteTextView.setText("")
                    }
                }
                editDialog.autoCompleteTextView.setAdapter(customAdapter)
            }
        }
        val dialog = Dialog(this)
        dialog.setContentView(editDialog.root)
        editDialog.dialogBtnNo.setOnClickListener {
            updateNewBoard(board = board.copy(assignedTo = assignTo as ArrayList<String>)) {}
            dialog.dismiss()
        }
        editDialog.dialogBtnYes.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun addChip(text: String, bottomSheetBinding: EditAssigneesBinding) {
        assignTo.add(text)
        val chip = Chip(bottomSheetBinding.chipGroup.context)
        chip.text = text
        chip.isClickable = true
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            suggestions += text
            customAdapter?.submitList(suggestions as ArrayList<String>)
            bottomSheetBinding.chipGroup.removeView(chip)
        }

        bottomSheetBinding.chipGroup.addView(chip)
    }

    private fun Dialogfunction(board: Board) {
        var isCorrectName = false
        var isCorrectDes = false
        var user = Firebase.auth.currentUser
        val bottomSheetBinding = AddTaskBottomSheetBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.textInputLayoutBoardnameEdit.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 2) {
                    bottomSheetBinding.textInputLayoutBoardnameEdit.error = null
                    isCorrectName = true
                } else {
                    bottomSheetBinding.textInputLayoutBoardnameEdit.error =
                        "Enter Correct Task Name"
                    isCorrectName = false
                }
            }
        })
        bottomSheetBinding.textInputLayoutBoarddescEdit.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.isNotEmpty()) {
                    bottomSheetBinding.textInputLayoutBoarddescEdit.error = null
                    isCorrectDes = true
                } else {
                    bottomSheetBinding.textInputLayoutBoarddescEdit.error =
                        "Add Description"

                    isCorrectDes = false
                }
            }
        })
        bottomSheetBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        bottomSheetBinding.addBtn.setOnClickListener {
            if (isCorrectName && isCorrectDes) {
                addNewTask(
                    task = Task(
                        title = bottomSheetBinding.textInputLayoutBoardnameEdit.text.toString(),
                        desc = bottomSheetBinding.textInputLayoutBoarddescEdit.text.toString(),
                        lastEdit = user?.email.toString(),
                        state = State.todo
                    ),
                    context = applicationContext,
                    board = Board(
                        name = board.name,
                        des = board.des,
                        createdBy = board.createdBy,
                        assignedTo = board.assignedTo
                    )
                ) {
                    dialog.dismiss()
                    Toast.makeText(this, "Task Created Successfully", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fill Data Properly", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
}