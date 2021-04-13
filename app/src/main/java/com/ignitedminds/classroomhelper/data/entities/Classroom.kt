package com.ignitedminds.classroomhelper.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "classrooms")
data class Classroom(
    @PrimaryKey
    val _id: String,
    val name : String,
    val code : String,
    val totalStudents : Int
)