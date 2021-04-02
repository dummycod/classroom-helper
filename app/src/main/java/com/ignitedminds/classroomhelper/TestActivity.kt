package com.ignitedminds.classroomhelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    override fun onBackPressed() {
        Log.d("TAG", "onBackPressed: preed")
        super.onBackPressed()
    }

}