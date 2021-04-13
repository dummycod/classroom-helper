package com.ignitedminds.classroomhelper.models

import android.app.Activity
import android.util.Log
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.messaging.FirebaseMessaging
import com.ignitedminds.classroomhelper.App
import com.ignitedminds.classroomhelper.Utils.SharedPrefsManager
import com.ignitedminds.classroomhelper.Utils.Utils
import com.ignitedminds.classroomhelper.interfaces.UI.OtpFragmentInterface
import com.ignitedminds.classroomhelper.interfaces.model.OtpModelInterface
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OtpModel(override val otpFragmentInterface: OtpFragmentInterface) :
    OtpModelInterface {

    private val URL = "http://192.168.1.7:3000/users/getuser"

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            Log.d("Message", "onVerificationCompleted: ")
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            otpFragmentInterface.apply {
                hideProgressBar()
                showDialog("Error","Try Again After Some Time")
                enableAllButtons()
            }
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            otpFragmentInterface.apply {
                hideProgressBar()
                setVerificationId(p0)
                enableAllButtons()
                disableResendButton()
                showDialog("Code Sent","Please Check Your Message Box For The Code")
            }
        }

    }

    override fun confirm(activity: Activity?, verificationId : String, otp : String) {
        if(!Utils.isOnline(App.context)){
            otpFragmentInterface.showDialog("No Internet","Please Connect To Internet")
            return
        }
        if(!Utils.validateOTP(otp)){
            otpFragmentInterface.showDialog("Error","Invalid Otp")
            return
        }

        activity?: return
        val credential = PhoneAuthProvider.getCredential(verificationId,otp)
        val auth = FirebaseAuth.getInstance()

        auth.signInWithCredential(credential).addOnCompleteListener(activity){task ->
            if(task.isSuccessful){
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
                    if(it.isSuccessful){
                        FirebaseMessaging.getInstance().token.addOnCompleteListener {
                            if(it.isSuccessful){
                                SharedPrefsManager.setRegistrationToken(App.context,it.result!!)
                                Log.d("TOKEN", "confirm: ${it.result}")
                                Utils.getInstance(App.context).addToRequestQueue(createJsonObjectRequest())
                            }else{
                                otpFragmentInterface.showDialog("Try Again","Some Error Occurred Please Try Again")
                            }
                        }
                    }else{
                        otpFragmentInterface.showDialog("Try Again","Some Error Occurred Please Try Again")
                    }
                }
            }else{
                if(task.exception is FirebaseAuthInvalidCredentialsException)
                    otpFragmentInterface.showDialog("Incorrect","Please Enter Correct Otp")
                else
                    otpFragmentInterface.showDialog("Error","Try Resending The OTP")
            }
        }
    }

    override fun resendCode(activity:Activity?,phoneNumber: String) {
        if(!Utils.isOnline(App.context)){
            otpFragmentInterface.showDialog("No Internet","Please Connect To Internet")
            return
        }
        activity?: return
        val auth = FirebaseAuth.getInstance()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(5L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun createJSONObject() : JSONObject{
        val jsonBody = JSONObject()
        val phoneNumber = otpFragmentInterface.getPhoneNumber()
        val registrationToken = SharedPrefsManager.getRegistrationToken(App.context)
        jsonBody.put("phoneNo",phoneNumber)
        jsonBody.put("registrationToken",registrationToken)
        return jsonBody
    }

    private fun createJsonObjectRequest() : JsonObjectRequest{
        return JsonObjectRequest(POST,URL,createJSONObject(), {
            if(it==null){
                otpFragmentInterface.apply{
                    Log.d("OtpModel", "createJsonObjectRequest: HERE")
                    showDialog("Error","Some Error Occurred Please Try Again..")
                    enableAllButtons()
                    hideProgressBar()
                }
            }else{
                val newUser = it.getBoolean("newUser")
                if(!newUser){
                    val userModel = Utils.getUserDataFromJson(it)
                    otpFragmentInterface.startLoginScreen(userModel)
                }else{
                    otpFragmentInterface.startRegistrationScreen()
                }
            }
        }, {
            otpFragmentInterface.apply {
                Log.d("TAG", "createJsonObjectRequest: "+it.message)
                showDialog("Error","Some Error Occurred Please Try Again..")
                enableAllButtons()
                hideProgressBar()
            }
        })
    }
}