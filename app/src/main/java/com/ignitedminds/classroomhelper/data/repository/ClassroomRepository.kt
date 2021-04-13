package com.ignitedminds.classroomhelper.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.ignitedminds.classroomhelper.Utils.Resource
import com.ignitedminds.classroomhelper.data.entities.Classroom
import com.ignitedminds.classroomhelper.data.entities.ClassroomList
import com.ignitedminds.classroomhelper.data.local.ClassroomDao
import com.ignitedminds.classroomhelper.data.remote.ClassroomRemoteDataSource
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ClassroomRepository @Inject constructor(
    private val localDataSource: ClassroomDao,
    private val remoteDataSource: ClassroomRemoteDataSource
) {
    val allClassrooms: LiveData<List<Classroom>> =
        localDataSource.getAllClassrooms()

    val progress: MutableLiveData<Resource<ClassroomList>> by lazy {
        MutableLiveData<Resource<ClassroomList>>()
    }

    suspend fun getClassroomsFromRemote() {
        //Pretend The Data Is Loading
        progress.postValue(Resource.loading())
        val classroomsResource = remoteDataSource.getClassrooms()
        //If There Was Some Error Emit The Error Object
        if (classroomsResource.status == Resource.Status.ERROR) {
            progress.postValue(Resource.error(classroomsResource.message))
        } else if (classroomsResource.status == Resource.Status.SUCCESS) {
            //If All The Things Went Fine Update The Database
            val classroomList = classroomsResource.data
            if (classroomList != null) {
                for (item in classroomList.results) {
                    Log.d("ClassroomHelper", "getClassroomsFromRemote: "+item.toString())
                    localDataSource.insert(item)
                }
                progress.postValue(Resource.success(classroomList))
            }
        }
    }
}