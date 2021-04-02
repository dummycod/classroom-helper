package com.ignitedminds.classroomhelper.interfaces.model

import android.app.Activity
import com.ignitedminds.classroomhelper.interfaces.UI.LoginFragmentInterface

interface LoginModelInterface {
    val loginFragmentInterface : LoginFragmentInterface
    fun sendVerificationCode(activity: Activity?)
}