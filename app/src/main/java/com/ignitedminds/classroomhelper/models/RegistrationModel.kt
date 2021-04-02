package com.ignitedminds.classroomhelper.models

import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.ignitedminds.classroomhelper.App
import com.ignitedminds.classroomhelper.R
import com.ignitedminds.classroomhelper.Utils.Utils
import com.ignitedminds.classroomhelper.interfaces.UI.RegisterInterface
import com.ignitedminds.classroomhelper.interfaces.model.RegistrationInterfaceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class RegistrationModel(override val registrationFragment: RegisterInterface) :
    RegistrationInterfaceModel {

    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var middleName: String
    private lateinit var instituteName: String
    private lateinit var birthDate: String
    private lateinit var phoneNumber: String
    private var imageBitmap : Bitmap? = null

    private var TAG ="FirstRequest"
    private val URL = "http://192.168.1.7:3000/users/register"

    override fun registerUser() {

        if (!validateFields())
            return
        registrationFragment.showProgressBar()
        val jsonObject = createJSONObject()
        val jsonObjectRequest = createJsonRequest(jsonObject)
        Utils.getInstance(App.context.applicationContext).addToRequestQueue(jsonObjectRequest)
    }

    private fun createJsonRequest(jsonObject: JSONObject): JsonObjectRequest {
        return JsonObjectRequest(
            POST,
            URL,
            jsonObject,
            Response.Listener {
                if(it==null){
                    registrationFragment.showDialog("Error", "Error Occurred Please Try Again")
                    registrationFragment.hideProgressBar()
                    return@Listener
                }else{
                    val userModel = Utils.getUserDataFromJson(it)
                    registrationFragment.onSuccess(userModel)
                }
            },
            Response.ErrorListener {
                registrationFragment.showDialog("Error", "Error Occurred Please Try Again")
                registrationFragment.hideProgressBar()
                Log.d(TAG, "createStringRequest: Fail "+it?.networkResponse)
            })
    }

    private fun imageToString():String?{
        if(imageBitmap!=null){
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap!!.compress(Bitmap.CompressFormat.JPEG,70,byteArrayOutputStream)
            val imgBytes = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(imgBytes,Base64.DEFAULT)
        }
        return null
    }

    private fun createJSONObject(): JSONObject {
        val jsonBody = JSONObject()
        jsonBody.put("firstName", firstName)
        jsonBody.put("lastName", lastName)
        jsonBody.put("middleName", middleName)
        jsonBody.put("birthDate", birthDate)
        jsonBody.put("instituteName", instituteName)
        jsonBody.put("phoneNo", phoneNumber)

        val imageString = imageToString()
        if(imageString!=null){
            jsonBody.put("imageBase64",imageString)
        }
        return jsonBody
    }


    private fun validateFields(): Boolean {

        registrationFragment.apply {
            firstName = getFirstName()
            lastName = getLastName()
            middleName = getMiddleName()
            instituteName = getInstitutionName()
            birthDate = getBirthDate()
            imageBitmap = getPhotoBitmap()
            phoneNumber = getPhoneNumber()
        }

        val age = registrationFragment.getAge()
        val emptyError = App.context.getString(R.string.field_cannot_be_empty)
        var flag = true
        if (firstName.isEmpty()) {
            registrationFragment.setFirstNameError(emptyError)
            flag = false
        }
        if (lastName.isEmpty()) {
            registrationFragment.setLastNameError(emptyError)
            flag = false
        }

        if (instituteName.isEmpty()) {
            registrationFragment.setInstitutionError(emptyError)
            flag = false
        }

        if (birthDate.isEmpty()) {
            registrationFragment.setBirthDateError(emptyError)
            flag = false
        }else if (age < 5) {
            registrationFragment.showDialog("Error",App.context.applicationContext.getString(R.string.age_minimum))
            flag = false
        }
        return flag
    }

}