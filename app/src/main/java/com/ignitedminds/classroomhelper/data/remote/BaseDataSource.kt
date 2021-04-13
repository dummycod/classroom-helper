package com.ignitedminds.classroomhelper.data.remote

import com.ignitedminds.classroomhelper.Utils.Resource
import retrofit2.Response

abstract class BaseDataSource {
    protected suspend fun <T> getResult(call: suspend()-> Response<T>): Resource<T>{
        try{
            val response = call()
            if(response.isSuccessful){
                val body = response.body()
                if(body!=null)
                    return Resource.success(body)
            }
            return error("${response.code()} ${response.message()}")
        }catch(e: Exception){
            return error(e.message ?: e.toString())
        }
    }

    private fun<T> error(message: String): Resource<T>{
        return Resource.error("Network call failed reason : $message ")
    }
}