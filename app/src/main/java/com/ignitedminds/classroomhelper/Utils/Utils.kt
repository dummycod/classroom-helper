package com.ignitedminds.classroomhelper.Utils

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.ignitedminds.classroomhelper.App
import com.ignitedminds.classroomhelper.R
import com.ignitedminds.classroomhelper.models.UserModel
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException



class Utils private constructor(context: Context) {

    companion object {
        @Volatile //Only One Copy Of Volatile In Main Memory Visible To All Threads
        private var INSTANCE: Utils? = null

        //Double Checked Locking...
        /*
        * We only acquire lock on object once when object is null
        * This methods drastically reduces the overhead of calling the synchronized
        * method every time (Since synchronized increases waiting time..)
        *
        * */

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Utils(context).also {
                INSTANCE = it
            }
        }

        @Suppress("DEPRECATION")
        fun isOnline(ctx: Context?): Boolean {
            var result = false
            ctx ?: return result
            val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = cm.activeNetwork ?: return false
                val actNw = cm.getNetworkCapabilities((networkCapabilities)) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                cm.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
            return result
        }

        fun isPhoneNumberValid(countryCode: String, phoneNo: String): Boolean {
            val phoneNumberUtil = PhoneNumberUtil.getInstance()
            val isoCode = phoneNumberUtil.getRegionCodeForCountryCode(countryCode.toInt())
            var phoneNumber: Phonenumber.PhoneNumber? = null

            try {
                phoneNumber = phoneNumberUtil.parse(phoneNo, isoCode)
            } catch (e: Exception) {
                Log.d("Error", "Error Occurred !")
                return false
            }
            return phoneNumberUtil.isValidNumber(phoneNumber)
        }

        fun validateOTP(otp: String): Boolean {
            return otp.length == 6
        }

        fun validateName(name: String): String {
            if(name.isEmpty()){
                return App.context.applicationContext.getString(R.string.field_cannot_be_empty)
            }else if(name.contains(" ")){
                return App.context.applicationContext.getString(R.string.name_no_spaces)
            }
            return ""
        }

        fun validateInstituteName(name: String): Boolean {
            return false
        }

        fun validateAge(age: Int): Boolean {
            return age > 3
        }

        fun showDialog(context: Context?, title: CharSequence, message: CharSequence) {
            context ?: return
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        @Throws(IOException::class)
        fun createImageFile(): File? {
            val storageDir : File? = App.context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "profile_photo",
                ".jpg",
                storageDir
            )
        }


        suspend fun writeImageToFileAsync(bitmap: Bitmap) : Uri? = coroutineScope{
            val result = kotlin.runCatching {
                val file = createImageFile()
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG,50,bos)
                val byteArray= bos.toByteArray()
                val fos = FileOutputStream(file)
                fos.write(byteArray)
                fos.flush()
                fos.close()
                if(file!=null)
                    FileProvider.getUriForFile(App.context,"com.ignitedminds.fileprovider",file)
                else{
                    null
                }
            }
            result.getOrNull()
        }

        fun getUserDataFromJson(json : JSONObject?) :UserModel {
            return UserModel(
                json?.getString("_id")?:"",
                json?.getString("firstName")?:"",
                json?.getString("middleName")?:"",
                json?.getString("lastName")?:"",
                json?.getString("birthDate")?:"",
                json?.getString("phoneNo")?:"",
                json?.getString("instituteName")?:"",
                json?.getString("imageBase64")?:""
            )
        }

    }

    private val requestQueue : RequestQueue by lazy{
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>){
        requestQueue.add(req)
    }
}