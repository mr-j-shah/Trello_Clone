package com.crestinfosystems_jinay.trello.Screens_Activity.setting.projects

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.test.core.app.ApplicationProvider
import com.crestinfosystems_jinay.trello.MainActivity
import com.crestinfosystems_jinay.trello.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseMessageReceiver : FirebaseMessagingService() {
    override fun onMessageReceived(rm: RemoteMessage) {
        // create notification channel
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            var channel = NotificationChannel("MyNotification","MyNotification",
                NotificationManager.IMPORTANCE_DEFAULT)
            var manager = getSystemService(NotificationManager::class.java) as NotificationManager;
            manager.createNotificationChannel(channel)
        }
        //log data
        Log.e("TAG", "onMessageReceived: "+rm.data["title"]+rm.data["description"]+rm.data["body"], )
        showNotification(rm.data["title"],rm.data["description"])

        super.onMessageReceived(rm);
    }

    private fun showNotification(title: String?, desc: String?) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var name = prefs.getString("signature", "");

        val notificationIntent =
            Intent(applicationContext, MainActivity::class.java)
        //create pending intent
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        //create builder
        var builder = NotificationCompat.Builder(this,"MyNotification")
            .setContentTitle("Hi $name,$title")
            .setSmallIcon(R.drawable.login_checkmark)
            .setContentText(desc)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
        var manager = NotificationManagerCompat.from(this)
        manager.apply {
            if (ActivityCompat.checkSelfPermission(
                    application,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(123,builder.build())
        }
    }
}
