package com.ignitedminds.classroomhelper.di

import android.app.Application
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ignitedminds.classroomhelper.data.local.ClassroomDao
import com.ignitedminds.classroomhelper.data.local.HelperDatabase
import com.ignitedminds.classroomhelper.data.remote.ClassroomRemoteDataSource
import com.ignitedminds.classroomhelper.data.remote.ClassroomService
import com.ignitedminds.classroomhelper.data.repository.ClassroomRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application) =
        Room.databaseBuilder(app, HelperDatabase::class.java, "helper_database")
            .fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideClassroomDao(db: HelperDatabase) = db.classroomDao()

    @Provides
    fun provideGSON(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit =
        Retrofit.Builder().baseUrl("http://192.168.1.7:3000/")
            .addConverterFactory(GsonConverterFactory.create(gson)).build()

    @Provides
    fun provideClassroomService(retrofit: Retrofit): ClassroomService =
        retrofit.create(ClassroomService::class.java)

    @Provides
    fun provideClassroomRemoteDataSource(classroomService: ClassroomService) =
        ClassroomRemoteDataSource(classroomService)

    @Provides
    @Singleton
    fun provideClassroomRepository(
        localDataSource: ClassroomDao,
        remoteDataSource: ClassroomRemoteDataSource
    ) = ClassroomRepository(localDataSource, remoteDataSource)

}