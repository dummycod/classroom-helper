package com.ignitedminds.classroomhelper.models

import android.app.Activity
import android.os.Looper
import android.util.Log
import android.os.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.messaging.FirebaseMessaging
import com.ignitedminds.classroomhelper.App
import com.ignitedminds.classroomhelper.Utils.Utils
import com.ignitedminds.classroomhelper.interfaces.UI.LoginFragmentInterface
import com.ignitedminds.classroomhelper.interfaces.model.LoginModelInterface
import java.util.concurrent.TimeUnit
import com.ignitedminds.classroomhelper.R

class LoginModel(override val loginFragmentInterface: LoginFragmentInterface) :
    LoginModelInterface {

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            loginFragmentInterface.hideProgressBar()

        }

        override fun onVerificationFailed(e: FirebaseException) {
            loginFragmentInterface.apply {
                hideProgressBar()
                showDialog("Failed", "Try Again After Some Time")
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            loginFragmentInterface.onCodeSent(verificationId)
            Log.d("Validation", "onCodeSent:$verificationId")
        }

        override fun onCodeAutoRetrievalTimeOut(p0: String) {
            super.onCodeAutoRetrievalTimeOut(p0)
        }
    }


    override fun sendVerificationCode(activity: Activity?) {
        activity ?: return
        loginFragmentInterface.apply {
            if(!Utils.isOnline(activity)){
                showDialog("No Internet","Please Connect To Internet")
                return
            }
            if(getPhoneNumber().isEmpty()){
               setError("This Field Can't Be Empty")
                return
            }
            if (!Utils.isPhoneNumberValid(getCountryCodes(), getPhoneNumber())) {
                setError(App.context.resources.getString(R.string.invalid_phone_number))
            } else {
                showProgressBar()
                val handler = Handler(Looper.getMainLooper())
                val runnable = Runnable {
                    val phoneNumber = "+"+getCountryCodes() + getPhoneNumber()
                    Log.d("Verification ",phoneNumber)
                    val auth = FirebaseAuth.getInstance()
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(5L, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(callbacks)
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                }

                handler.postDelayed(runnable,4000)

            }

        }
    }
}