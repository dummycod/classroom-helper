package com.ignitedminds.classroomhelper.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ignitedminds.classroomhelper.R
import com.ignitedminds.classroomhelper.data.entities.Classroom

class ClassroomAdapter(private val context: Context): RecyclerView.Adapter<ClassroomAdapter.ViewHolder>() {

    val allClassrooms = ArrayList<Classroom>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val classroomName = itemView.findViewById<TextView>(R.id.classroom_name)
        val totalStudents = itemView.findViewById<TextView>(R.id.count)
        val code = itemView.findViewById<TextView>(R.id.code)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassroomAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.classroom_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentClassroom = allClassrooms[position]
        holder.classroomName.text = currentClassroom.name
        holder.totalStudents.text = currentClassroom.totalStudents.toString()
        holder.code.text = currentClassroom.code
    }

    override fun getItemCount(): Int {
        return allClassrooms.size
    }

    fun updateList(newList: List<Classroom>){
        allClassrooms.clear()
        allClassrooms.addAll(newList)
        notifyDataSetChanged()
    }
}