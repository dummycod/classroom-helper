package com.ignitedminds.classroomhelper.data.remote

import javax.inject.Inject

class ClassroomRemoteDataSource @Inject constructor(private val classroomService: ClassroomService) :
    BaseDataSource() {

        suspend fun getClassrooms() = getResult { classroomService.getAllClassrooms() }
        suspend fun  getClassroom(id : String) = getResult { classroomService.getClassroom(id) }
}