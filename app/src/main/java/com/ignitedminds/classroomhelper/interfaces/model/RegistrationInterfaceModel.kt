package com.ignitedminds.classroomhelper.interfaces.model

import com.ignitedminds.classroomhelper.interfaces.UI.RegisterInterface

interface RegistrationInterfaceModel {
    val registrationFragment : RegisterInterface
    fun registerUser()
}