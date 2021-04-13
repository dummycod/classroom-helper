package com.ignitedminds.classroomhelper.data.remote

import com.ignitedminds.classroomhelper.data.entities.Classroom
import com.ignitedminds.classroomhelper.data.entities.ClassroomList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ClassroomService {

    @GET("classrooms")
    suspend fun getAllClassrooms(): Response<ClassroomList>

    @GET("classrooms/{id}")
    suspend fun  getClassroom(@Path("id") id: String) : Response<Classroom>
}