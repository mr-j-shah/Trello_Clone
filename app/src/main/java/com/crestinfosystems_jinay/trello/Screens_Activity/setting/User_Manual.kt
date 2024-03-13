package com.crestinfosystems_jinay.trello.Screens_Activity.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.crestinfosystems_jinay.trello.databinding.ActivityUserManualBinding

class User_Manual : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var binding: ActivityUserManualBinding = ActivityUserManualBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbarExercise)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbarExercise.setNavigationOnClickListener {
            onBackPressed()
        }
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}