package com.ignitedminds.classroomhelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {
            if(!it.isSuccessful) {
                Log.d("TestActivity", "onCreate: FCM FAILED")
                return@OnCompleteListener
            }
            val token = it.result
            Log.d("TestActivity", "onCreate: $token")
        })
    }

    override fun onBackPressed() {
        Log.d("TAG", "onBackPressed: preed")
        super.onBackPressed()
    }

}