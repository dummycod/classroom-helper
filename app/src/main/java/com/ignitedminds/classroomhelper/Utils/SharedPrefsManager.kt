package com.ignitedminds.classroomhelper.Utils

import android.content.Context
import android.content.SharedPreferences
import com.ignitedminds.classroomhelper.models.UserModel

// DataStore May Be Used In Future Instead Of SharedPreferences

class SharedPrefsManager {
    companion object {
        private const val PHONE_NUMBER = "PHONE_NUMBER"
        private const val LOGGED_IN = "LOGGED_IN"
        private const val LOGIN_DETAILS = "LOGIN_DETAILS"
        private const val FIRST_NAME = "FIRST_NAME"
        private const val MIDDLE_NAME = "MIDDLE_NAME"
        private const val LAST_NAME = "LAST_NAME"
        private const val INSTITUTE = "INSTITUTE"
        private const val BIRTHDATE = "BIRTHDATE"
        private const val PROFILE_PHOTO_PATH = "PATH"
        private const val USERID ="USERID"
        private const val REGISTRATION_TOKEN = "REGISTRATION_TOKEN"

        private fun getSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(LOGIN_DETAILS, Context.MODE_PRIVATE)
        }

        fun getLoginStatus(context: Context): Boolean {
            val sharedPrefs = getSharedPreferences(context)
            return sharedPrefs.getBoolean(LOGGED_IN, false)
        }

        fun setLoginStatus(context: Context, loggedIn: Boolean) {
            val editor = getSharedPreferences(context).edit()
            editor.putBoolean(LOGGED_IN, loggedIn)
            editor.apply()
        }

        fun getString(NAME: String, context: Context): String? {
            val sharedPrefs = getSharedPreferences(context)
            return sharedPrefs.getString(NAME, null)
        }

        fun setString(NAME: String, context: Context, firstName: String) {
            val editor = getSharedPreferences(context).edit()
            editor.putString(NAME, firstName)
            editor.apply()
        }

        fun getFirstName(context: Context) = getString(FIRST_NAME, context)
        fun getMiddleName(context: Context) = getString(MIDDLE_NAME, context)
        fun getLastName(context: Context) = getString(LAST_NAME, context)
        fun getPhoneNumber(context: Context) = getString(PHONE_NUMBER, context)
        fun getInstitute(context: Context) = getString(INSTITUTE, context)
        fun getBirthDate(context: Context) = getString(BIRTHDATE, context)
        fun getProfilePhotoPath(context: Context) = getString(PROFILE_PHOTO_PATH, context)
        fun getUserId(context: Context ) = getString(USERID,context)
        fun getRegistrationToken(context:Context) = getString(REGISTRATION_TOKEN,context)

        fun setFirstName(context: Context, firstName: String) =
            setString(FIRST_NAME, context, firstName)

        fun setMiddleName(context: Context, middleName: String) =
            setString(MIDDLE_NAME, context, middleName)

        fun setLastName(context: Context, lastName: String) =
            setString(LAST_NAME, context, lastName)

        fun setPhoneNumber(context: Context, phoneNumber: String) =
            setString(PHONE_NUMBER, context, phoneNumber)

        fun setInstitute(context: Context, institute: String) =
            setString(INSTITUTE, context, institute)

        fun setBirthDate(context: Context, birthDate: String) =
            setString(BIRTHDATE, context, birthDate)

        fun setProfilePhotoPath(context: Context, path: String) =
            setString(PROFILE_PHOTO_PATH, context, path)

        fun setUserId(context: Context,userid : String)=
            setString(USERID,context,userid)

        fun setRegistrationToken(context: Context,token:String) =
            setString(REGISTRATION_TOKEN,context,token)

        fun saveStringData(context:Context,user : UserModel){
            setUserId(context,user._id)
            setFirstName(context,user.firstName)
            setMiddleName(context,user.middleName)
            setLastName(context,user.lastName)
            setBirthDate(context,user.birthDate)
            setPhoneNumber(context,user.phoneNumber)
            setInstitute(context,user.instituteName)
            setProfilePhotoPath(context,"")
        }
    }
}