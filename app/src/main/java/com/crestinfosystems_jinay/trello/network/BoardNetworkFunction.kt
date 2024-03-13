package com.crestinfosystems_jinay.trello.network

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.crestinfosystems_jinay.trello.data.Board
import com.crestinfosystems_jinay.trello.data.Task
import com.crestinfosystems_jinay.trello.data.UserData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


val database = FirebaseFirestore.getInstance();
val realTimeDatabase = FirebaseDatabase.getInstance()

fun createNewBoard(board: Board, onTap: () -> Unit) {
    if (board.name != null) {
        database.collection("Boards").document(board.name.toString())
            .set(board, SetOptions.merge()).addOnSuccessListener {
                onTap()
            }
        realTimeDatabase.reference.child("Projects").child(board.name).setValue(board)
    }
}

fun updateNewBoard(board: Board, onTap: () -> Unit) {
    if (board.name != null) {
        database.collection("Boards").document(board.name.toString())
            .update(board.toMap()).addOnSuccessListener {
                onTap()
            }
        realTimeDatabase.reference.child("Projects").child(board.name).updateChildren(board.toMap())
    }
}

fun addNewTask(task: Task, board: Board, context: Context, onTap: () -> Unit) {
    val preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    val editor: Editor = preferences.edit()
    editor.putString("lastedit", Gson().toJson(task.toMap()));
    editor.apply();

    if (task.title != null) {
        realTimeDatabase.reference.child("Projects").child(board.name).child("task").push()
            .setValue(task).addOnSuccessListener {
                onTap()
            }
    }
}

fun updateNewTask(task: Task, board: Board, context: Context, onTap: () -> Unit) {
    val preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    val editor: Editor = preferences.edit()
    editor.putString("lastedit", Gson().toJson(task.toMap()));
    editor.apply();
    if (task.title != null) {
        realTimeDatabase.reference.child("Projects").child(board.name).child("task").child(task.key)
            .updateChildren(task.toMap()).addOnSuccessListener {
                onTap()
            }
    }
}

fun deleteNewTask(task: Task, board: Board, context: Context, onTap: () -> Unit) {
    val preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    val editor: Editor = preferences.edit()
    editor.putString("lastedit", Gson().toJson(task.toMap()));
    editor.apply();
    if (task.title != null) {
        realTimeDatabase.reference.child("Projects").child(board.name).child("task").child(task.key)
            .removeValue().addOnSuccessListener {
                onTap()
            }
    }
}

suspend fun readUserAllUserOnApplication(): List<String>? {
    return suspendCancellableCoroutine { continuation ->
        val ref: CollectionReference = database.collection("Users")
        ref.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userListEmail: MutableList<String> = mutableListOf()
                    for (document in task.result!!) {
                        val userData = UserData.toObj(document.data)
                        userListEmail.add(userData.email!!)
                    }
                    continuation.resume(userListEmail)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resume(null)
            }
    }
}

suspend fun readProjectsByUserEmail(email: String): List<Board>? {
    return suspendCancellableCoroutine { continuation ->
        val ref: CollectionReference = database.collection("Boards")
        ref.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userListEmail: MutableList<Board> = mutableListOf()
                    for (document in task.result!!) {
                        val boardData: MutableMap<String, Any> = document.data
                        if ((boardData["assignedTo"] as List<String>).contains(email)) {
                            userListEmail.add(Board.toObj(boardData))
                        }
                    }
                    continuation.resume(userListEmail)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resume(null)
            }
    }
}

suspend fun readAllProjectsByUserEmail(email: String): List<Board>? {
    return suspendCancellableCoroutine { continuation ->
        val ref: CollectionReference = database.collection("Boards")
        ref.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userListEmail: MutableList<Board> = mutableListOf()
                    for (document in task.result!!) {
                        val boardData: MutableMap<String, Any> = document.data
                        if ((boardData["assignedTo"] as List<String>).contains(email)) {
                            userListEmail.add(Board.toObj(boardData))
                        }
                        if (
                            (boardData["createdBy"] as String) == email
                        ) {
                            userListEmail.add(Board.toObj(boardData))
                        }
                    }
                    continuation.resume(userListEmail)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resume(null)
            }
    }
}