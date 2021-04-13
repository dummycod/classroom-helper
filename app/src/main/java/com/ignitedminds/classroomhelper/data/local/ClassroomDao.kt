package com.ignitedminds.classroomhelper.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ignitedminds.classroomhelper.data.entities.Classroom
@Dao
interface ClassroomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(classroom: Classroom)

    @Delete
    suspend fun delete(classroom: Classroom)

    @Query("SELECT * FROM classrooms")
    fun getAllClassrooms(): LiveData<List<Classroom>>

}