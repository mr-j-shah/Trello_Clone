package com.crestinfosystems_jinay.trello

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.crestinfosystems_jinay.trello.Screens_Activity.HomePage
import com.crestinfosystems_jinay.trello.databinding.ActivityMainBinding
import com.crestinfosystems_jinay.trello.splash_pages.splash_screen
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {


    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        var versionName = applicationContext.packageManager.getPackageInfo(
            applicationContext.packageName,
            0
        ).versionName
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            val user = mAuth.currentUser
            if (user != null) {
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
                this.finish()
            } else {
                val intent: Intent = Intent(this, splash_screen::class.java)
                startActivity(intent)
                finish() // Op
            }
        }, 3000)
        binding?.appVersion?.setText("v " + versionName);
    }
}