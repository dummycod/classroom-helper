package com.ignitedminds.classroomhelper.interfaces.UI

import android.graphics.Bitmap
import com.ignitedminds.classroomhelper.models.UserModel

interface RegisterInterface {
    fun showProgressBar()
    fun hideProgressBar()
    fun saveData()
    fun onSuccess(userModel: UserModel)
    fun getFirstName(): String
    fun getLastName(): String
    fun getMiddleName(): String
    fun getInstitutionName(): String
    fun getBirthDate(): String
    fun showAlert()
    fun getAge(): Int
    fun getPhoneNumber(): String
    fun getPhotoBitmap(): Bitmap?
    fun setFirstNameError(error : String)
    fun setMiddleNameError(error : String)
    fun setLastNameError(error : String)
    fun setInstitutionError(error : String)
    fun setBirthDateError(error : String)
    fun showDialog(title: CharSequence, message: CharSequence)
}