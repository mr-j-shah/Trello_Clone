package com.crestinfosystems_jinay.trello

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class Trello: Application() {
    override fun onCreate() {

        Log.e("TAG", "onCreate: CREATED", )

        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e("TAG", "onCreate: subscribeToTopic", )
                }else{
                    Log.e("TAG", "onCreate: subscribeToTopic failed", )
                }
            }
        super.onCreate()
    }
}