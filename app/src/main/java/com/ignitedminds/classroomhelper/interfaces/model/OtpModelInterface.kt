package com.ignitedminds.classroomhelper.interfaces.model

import android.app.Activity
import com.ignitedminds.classroomhelper.interfaces.UI.OtpFragmentInterface

interface OtpModelInterface {
    val otpFragmentInterface: OtpFragmentInterface
    fun confirm(activity: Activity?,verificationId : String,otp : String)
    fun resendCode(activity:Activity?,phoneNumber:String)

}