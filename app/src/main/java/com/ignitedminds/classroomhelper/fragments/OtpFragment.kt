package com.ignitedminds.classroomhelper.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ignitedminds.classroomhelper.R
import com.ignitedminds.classroomhelper.Utils.GenericOnKeyListener
import com.ignitedminds.classroomhelper.Utils.GenericOnKeyListenerOnBackPressed
import com.ignitedminds.classroomhelper.Utils.SharedPrefsManager
import com.ignitedminds.classroomhelper.Utils.Utils
import com.ignitedminds.classroomhelper.databinding.FragmentOtpBinding
import com.ignitedminds.classroomhelper.interfaces.UI.OtpFragmentInterface
import com.ignitedminds.classroomhelper.models.OtpModel
import com.ignitedminds.classroomhelper.models.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class OtpFragment : Fragment(),
    OtpFragmentInterface,GenericOnKeyListenerOnBackPressed,CoroutineScope {
    private lateinit var binding: FragmentOtpBinding
    private var handler = Handler(Looper.getMainLooper())
    private var seconds = 10
    private var running = true
    private val args: OtpFragmentArgs by navArgs()
    private val otpModel = OtpModel(this)
    private lateinit var phoneNumber: String
    private lateinit var verificationId: String
    private val TAG = "OtpFragment"
    private lateinit var job : Job

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showAlert()
        }

    }
    private var actionListener = TextView.OnEditorActionListener{v,i,e->
        if(i==EditorInfo.IME_ACTION_DONE){
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken,0)
            true
        }
        false
    }

    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_otp, container, false)
        setListeners()

        phoneNumber = args.phoneNumber
        SharedPrefsManager.setPhoneNumber(requireContext(),phoneNumber)
        verificationId = args.verificationId
        binding.sentTextView.text = "Enter the 6-digit code we sent to $phoneNumber"
        job = Job()
        disableResendButton()
        addListenersToOtp()
        Log.d(TAG, "onCreateView: ")
        return binding.root
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }


    private fun setListeners() {
        binding.confirm.setOnClickListener {
            showProgressBar()
            disableAllButtons()
            otpModel.confirm(activity, verificationId, getOtp())
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken,0)
        }
        binding.changeNumber.setOnClickListener {
            changePhoneNumber()
        }
        binding.resendCode.setOnClickListener {
            showProgressBar()
            disableAllButtons()
            otpModel.resendCode(activity, phoneNumber)
        }
    }
///Yet To Be Solved.. For The Time I Left It Like This
    private fun addListenersToOtp() {
        GenericOnKeyListener.setUpWatcher(this)
        binding.apply {
            otp1.setOnEditorActionListener(actionListener)
            otp2.setOnEditorActionListener(actionListener)
            otp3.setOnEditorActionListener(actionListener)
            otp4.setOnEditorActionListener(actionListener)
            otp5.setOnEditorActionListener(actionListener)
            otp6.setOnEditorActionListener(actionListener)
            otp1.setOnKeyListener(GenericOnKeyListener(otp1))
            otp2.setOnKeyListener(GenericOnKeyListener(otp2))
            otp3.setOnKeyListener(GenericOnKeyListener(otp3))
            otp4.setOnKeyListener(GenericOnKeyListener(otp4))
            otp5.setOnKeyListener(GenericOnKeyListener(otp5))
            otp6.setOnKeyListener(GenericOnKeyListener(otp6))
        }
    }

    private fun setResendTimer() {

        val runnable = object : Runnable {
            override fun run() {
                if (seconds == 0 && context != null) {
                    enableResendButton()
                }
                if (seconds > 0 && running) {
                    seconds--
                    binding.resendCode.text = seconds.toString()
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(runnable)

    }

    override fun showDialog(title: CharSequence, message: CharSequence) {
        hideProgressBar()
        Utils.showDialog(context,title,message)
        enableAllButtons()
    }

    override fun changePhoneNumber() {
        findNavController().navigate(R.id.action_otpFragment_to_loginFragment)
    }
    override fun disableResendButton() {
        running = true
        binding.apply {
            resendCode.isClickable = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resendCode.setBackgroundColor(resources.getColor(R.color.grey, activity?.theme))
            } else {
                resendCode.setBackgroundColor(resources.getColor(R.color.grey))
            }
            setResendTimer()
        }

    }

    override fun enableResendButton() {
        seconds = 10
        running = false
        binding.apply {
            resendCode.text = "Resend"
            resendCode.isClickable = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resendCode.setBackgroundColor(
                    resources.getColor(
                        R.color.design_default_color_error,
                        activity?.theme
                    )
                )
            } else {
                resendCode.setBackgroundColor(resources.getColor(R.color.design_default_color_error))
            }
        }

    }

    override fun startRegistrationScreen() {
        val action = OtpFragmentDirections.actionOtpFragmentToRegistrationFragment()
        findNavController().navigate(action)
    }

    override fun startProfileScreen(userModel: UserModel) {
        SharedPrefsManager.saveStringData(requireContext(),userModel)
        val action = OtpFragmentDirections.actionOtpFragmentToProfileFragment()
        if(userModel.imageBase64String.isNullOrBlank()){
            hideProgressBar()
            SharedPrefsManager.setLoginStatus(requireContext(),true)
            Log.d(TAG, "startLoginScreen: User Logged In")
            findNavController().navigate(action)
        }else{
            val byteArray = Base64.decode(userModel.imageBase64String,Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
            launch(IO) {
                val uri = Utils.writeImageToFileAsync(bitmap)
                if(uri?.path!=null){
                    SharedPrefsManager.setProfilePhotoPath(requireContext(),uri.path!!)
                    SharedPrefsManager.setLoginStatus(requireContext(),true)
                }
                launch(Main) {
                    hideProgressBar()
                    findNavController().navigate(action)
                }
            }
        }
    }


    override fun getOtp(): String {
        val otp =
            binding.run { otp1.text.toString() + otp2.text + otp3.text.toString() + otp4.text.toString() + otp5.text.toString() + otp6.text.toString() }
        Log.d("Message", "getOtp: " + otp)
        return otp
    }

    override fun disableAllButtons() {
        binding.apply {
            confirm.isClickable = false
            resendCode.isClickable = false
            changeNumber.isClickable = false
        }
    }

    override fun enableAllButtons() {
        binding.apply {
            confirm.isClickable = true
            resendCode.isClickable = true
            changeNumber.isClickable = true
        }
    }

    override fun showProgressBar() {
        binding.circularProgressIndicator.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        binding.circularProgressIndicator.visibility = View.INVISIBLE
    }

    override fun showAlert() {
        val context = context ?: return
        MaterialAlertDialogBuilder(context)
            .setTitle("Exit")
            .setMessage("Are you sure you want to exit ?")
            .setNegativeButton("No") { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton("Yes") { dialog, which ->
                // Respond to positive button press
                activity?.finish()
            }
            .show()
    }

    override fun getPhoneNumber() :String {
        return phoneNumber
    }

    override fun setVerificationId(verificationId: String) {
        this.verificationId = verificationId
    }

    override fun onBackKeyPressed() {
        showAlert()
    }

}
