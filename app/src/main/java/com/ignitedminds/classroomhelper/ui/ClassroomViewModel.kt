package com.ignitedminds.classroomhelper.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitedminds.classroomhelper.Utils.Resource
import com.ignitedminds.classroomhelper.data.entities.Classroom
import com.ignitedminds.classroomhelper.data.repository.ClassroomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ClassroomViewModel @Inject constructor(private val classroomRepository: ClassroomRepository) :
    ViewModel() {
        val allClassrooms = classroomRepository.allClassrooms
        val progress = classroomRepository.progress

        fun updateClassroom() = viewModelScope.launch(Dispatchers.IO) {
            classroomRepository.getClassroomsFromRemote()
        }
}