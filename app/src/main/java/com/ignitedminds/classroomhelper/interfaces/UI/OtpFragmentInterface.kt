package com.ignitedminds.classroomhelper.interfaces.UI

import com.ignitedminds.classroomhelper.models.UserModel

interface OtpFragmentInterface {
    fun showDialog(title: CharSequence,message:CharSequence)
    fun changePhoneNumber()
    fun disableResendButton()
    fun enableResendButton()
    fun startRegistrationScreen()
    fun startProfileScreen(userModel: UserModel)
    fun getOtp() : String
    fun disableAllButtons()
    fun enableAllButtons()
    fun showProgressBar()
    fun hideProgressBar()
    fun showAlert()
    fun getPhoneNumber() : String
    fun setVerificationId(verificationId :String)
}