package com.ignitedminds.classroomhelper.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ignitedminds.classroomhelper.data.entities.Classroom

@Database(entities = [Classroom::class],version = 2,exportSchema = false)
abstract class HelperDatabase : RoomDatabase() {
    abstract fun classroomDao() : ClassroomDao
}