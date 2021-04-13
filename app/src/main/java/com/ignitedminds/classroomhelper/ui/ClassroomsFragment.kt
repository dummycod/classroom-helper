package com.ignitedminds.classroomhelper.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ignitedminds.classroomhelper.R
import com.ignitedminds.classroomhelper.Utils.Resource
import com.ignitedminds.classroomhelper.databinding.FragmentClassroomsBinding
import com.ignitedminds.classroomhelper.ui.adapters.ClassroomAdapter
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class ClassroomsFragment : Fragment() {

    private lateinit var binding : FragmentClassroomsBinding
    private val viewModel : ClassroomViewModel by viewModels()
    private lateinit var adapter : ClassroomAdapter
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =DataBindingUtil.inflate(inflater,R.layout.fragment_classrooms,container,false)
        val recyclerView = binding.recyclerView
        adapter = ClassroomAdapter(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        setUpListeners()
        setUpBroadcastReceiver()
        setUpObservers()
        return binding.root
    }

    private fun setUpBroadcastReceiver(){
        broadcastReceiver = object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                Log.d("TAG", "onReceive: ")
                viewModel.updateClassroom()
            }
        }
        activity?.registerReceiver(broadcastReceiver, IntentFilter("FcmTokenBroadcast"))
    }

    private fun setUpListeners(){
        binding.refresh.setOnClickListener{viewModel.updateClassroom()}
        binding.retry.setOnClickListener{viewModel.updateClassroom()}
    }

    private fun setUpObservers(){
        viewModel.allClassrooms.observe(viewLifecycleOwner, {
            it?.let {
                if(it.isEmpty()){
                    binding.emptyImage.visibility = View.VISIBLE
                    binding.noClassroomText.visibility = View.VISIBLE
                }else{
                    binding.emptyImage.visibility = View.GONE
                    binding.noClassroomText.visibility = View.GONE
                }
                adapter.updateList(it)
            }
        })

        viewModel.progress.observe(viewLifecycleOwner, {
            when(it.status){
                Resource.Status.LOADING->{
                    binding.apply {
                        progressCircular.visibility = View.VISIBLE
                        emptyImage.visibility = View.GONE
                        noClassroomText.visibility = View.GONE
                        errorText.visibility = View.GONE
                        retry.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                    }
                }
                Resource.Status.ERROR->{
                    binding.apply {
                        progressCircular.visibility = View.GONE
                        errorText.visibility = View.VISIBLE
                        retry.visibility = View.VISIBLE
                    }
                }
                Resource.Status.SUCCESS->{
                    binding.apply {
                        progressCircular.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}