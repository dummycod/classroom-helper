package com.ignitedminds.classroomhelper.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ignitedminds.classroomhelper.R
import com.ignitedminds.classroomhelper.Utils.Utils
import com.ignitedminds.classroomhelper.databinding.FragmentLoginBinding
import com.ignitedminds.classroomhelper.interfaces.UI.LoginFragmentInterface
import com.ignitedminds.classroomhelper.interfaces.model.LoginModelInterface
import com.ignitedminds.classroomhelper.models.LoginModel


class LoginFragment : Fragment(),
    LoginFragmentInterface {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var loginModelInterface: LoginModelInterface
    private val TAG= "LOGINFRAGMENT"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login, container, false
        )
        setListeners()
        loginModelInterface = LoginModel(this)
        return binding.root
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
    }

    private fun setListeners() {
        binding.button.setOnClickListener {
            getPhoneNumber()
                binding.button.isClickable=false
            loginModelInterface.sendVerificationCode(this.activity)
        }
    }

    override fun showDialog(title: CharSequence, message: CharSequence) {
        binding.button.isClickable=true
        Utils.showDialog(context,title,message)
    }

    override fun onCodeSent(verificationId: String) {
        hideProgressBar()
        val phoneNumber = "+"+getCountryCodes()+getPhoneNumber()
        Log.d(TAG, "onCodeSent: ")
        val action = LoginFragmentDirections.actionLoginFragmentToOtpFragment(verificationId,phoneNumber)
        findNavController().navigate(action)
    }

    override fun getPhoneNumber(): String {
        return binding.phoneNumber.editText?.text.toString()
    }

    override fun getCountryCodes(): String {
        return binding.ccp.selectedCountryCode
    }

    override fun showProgressBar() {
        binding.circularProgressIndicator.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        binding.circularProgressIndicator.visibility = View.INVISIBLE
    }

    override fun setError(error: CharSequence) {
        binding.button.isClickable=true
        binding.phoneNumber.error = error
    }

}