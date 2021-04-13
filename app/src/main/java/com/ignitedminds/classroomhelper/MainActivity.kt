package com.ignitedminds.classroomhelper

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ignitedminds.classroomhelper.Utils.SharedPrefsManager
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*Log.d("ACTIVITY", "onCreate: "+SharedPrefsManager.getFirstName(applicationContext))
        Log.d("ACTIVITY", "onCreate: "+SharedPrefsManager.getLastName(applicationContext))
        Log.d("ACTIVITY", "onCreate: "+SharedPrefsManager.getPhoneNumber(applicationContext))
        Log.d("ACTIVITY", "onCreate: "+SharedPrefsManager.getBirthDate(applicationContext))
        Log.d("ACTIVITY", "onCreate: "+SharedPrefsManager.getProfilePhotoPath(applicationContext))
        Log.d("ACTIVITY", "onCreate: "+SharedPrefsManager.getInstitute(applicationContext))
        Log.d("ACTIVITY", "onCreate: "+SharedPrefsManager.getUserId(applicationContext))*/
        /*val navHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.navigation)

        val prefs = getSharedPreferences("LOGIN_PREFS", Context.MODE_PRIVATE)

        val isOtpVerified = prefs.getBoolean("isOtpVerified",false)
        val phoneNumber = prefs.getString("phoneNumber","") ?: ""


        if(isOtpVerified && phoneNumber.isNotEmpty()){
            Log.d("Message", "User Verified")
        }else{
            graph.startDestination = R.id.loginFragment
            Log.d("Message", "onCreate: LoginFragment ")
        }
        navHostFragment.navController.graph=graph*/
    }


    override fun onStop() {
        super.onStop()
        Log.d("TAG", "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG", "onDestroy: ")
    }

    override fun onBackPressed() {
        Log.d("MainActivity", "onBackPressed: Called")
        super.onBackPressed()
    }
}