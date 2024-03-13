package com.crestinfosystems_jinay.trello.network

import android.util.Log
import com.crestinfosystems_jinay.trello.data.UserData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


class FirestoreDatabase {
    private lateinit var database: FirebaseFirestore

    init {
        database = FirebaseFirestore.getInstance();
    }

    fun updateUser(user: UserData, onFailure: () -> Unit) {
        var userMap: MutableMap<String, Any> = mutableMapOf()
        if (user.email != null) {
            userMap["email"] = user.email.toString()
        }
        if (user.name != null) {
            userMap["name"] = user.name.toString()
        }
        if (user.mobileNumber != null) {
            userMap["mobileNumber"] = user.mobileNumber.toString()
        }
        if (user.organization != null) {
            userMap["organization"] = user.organization.toString()
        }
        if (user.email != null) {
            database.collection("Users").document(user.email.toString())
                .update(userMap)
        } else {
            onFailure()
        }
    }

    fun writeNewUser(user: UserData) {
        if (user.email != null) {
            database.collection("Users").document(user.email.toString())
                .set(user.toMap())
        }
    }

    suspend fun readUser(email: String): UserData? {
        return suspendCancellableCoroutine { continuation ->
            val ref: DocumentReference = database.collection("Users").document(email)
            ref.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            val userData = UserData.toObj(document.data!!)
                            Log.d("Data of User", userData.toString())
                            continuation.resume(userData)
                        } else {
                            Log.d("Data of User", "Document does not exist")
                            continuation.resume(null)
                        }
                    } else {
                        Log.e("Data of User", "Error getting document", task.exception)
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Data of User", "User finding error", exception)
                    continuation.resume(null)
                }
        }
    }

    // How to call
    /*val userData = runBlocking {
        val result = readUser("user@example.com")
        // Use 'result' which is the retrieved UserData or null
        result?.let {
            // Do something with the UserData
            println("User Data: $it")
        } ?: run {
            // Handle case when UserData is null
            println("User not found")
        }
    }
    */
    fun readUserAndEntry(email: String, callBackFunction: () -> Unit) {
        val user = Firebase.auth.currentUser
        var ref: DocumentReference = database.collection("Users").document(email);
        ref.get().addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    var userData = UserData.toObj(document.data!!)
                    Log.d("Data of User", userData.toString())
                } else {
                    writeNewUser(
                        user = UserData(
                            email = user?.email ?: "",
                            name = user?.displayName ?: "",
                            mobileNumber = user?.phoneNumber ?: "",
                            organization = ""
                        )
                    )
                    Log.d("Data of User", "Document does not exist")
                }
                callBackFunction()
            } else {
                Log.e("Data of User", "Error getting document", task.exception)
            }
        })
            .addOnFailureListener(OnFailureListener { // if we do not get any data or any error we are displaying
                Log.e("Data of User", "User finding error")
            })

    }
}