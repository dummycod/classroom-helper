package com.ignitedminds.classroomhelper.interfaces.UI

import android.text.Editable
import android.widget.EditText

interface LoginFragmentInterface {
    fun setError(error: CharSequence)
    fun showDialog(title : CharSequence,message : CharSequence)
    fun onCodeSent(verificationId: String)
    fun getPhoneNumber() : String
    fun getCountryCodes() : String
    fun showProgressBar()
    fun hideProgressBar()
}