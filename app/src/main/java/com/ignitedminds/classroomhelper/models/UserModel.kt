package com.ignitedminds.classroomhelper.models

import java.util.*

data class UserModel(
    var _id : String,
    var firstName: String,
    var middleName: String,
    var lastName: String,
    var birthDate: String,
    var phoneNumber : String,
    var instituteName: String,
    var imageBase64String: String?
)