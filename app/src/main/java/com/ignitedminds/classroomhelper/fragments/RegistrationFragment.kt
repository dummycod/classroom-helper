package com.ignitedminds.classroomhelper.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ignitedminds.classroomhelper.R
import com.ignitedminds.classroomhelper.Utils.SharedPrefsManager
import com.ignitedminds.classroomhelper.Utils.Utils
import com.ignitedminds.classroomhelper.databinding.FragmentRegistrationBinding
import com.ignitedminds.classroomhelper.interfaces.UI.RegisterInterface
import com.ignitedminds.classroomhelper.interfaces.model.RegistrationInterfaceModel
import com.ignitedminds.classroomhelper.models.RegistrationModel
import com.ignitedminds.classroomhelper.models.UserModel
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*
import kotlin.coroutines.CoroutineContext

class RegistrationFragment : Fragment(), RegisterInterface,CoroutineScope {

    private lateinit var binding: FragmentRegistrationBinding
    private val calendar = Calendar.getInstance()
    private lateinit var registrationInterfaceModel: RegistrationInterfaceModel
    private val TAG = "RegistrationFragment"
    private var photoURI: Uri? = null
    private lateinit var job : Job

    private var bottomSheetDialog: BottomSheetDialog? = null
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showAlert()
        }
    }

    //If You Know.. You Know...
    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) {
        if (it) {
            startCamera()
        } else {
            showDialog(getString(R.string.permission_denied), getString(R.string.camera_needed))
            bottomSheetDialog?.dismiss()
        }
    }
    private val getImageGallery = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val imageUri = it.data?.data
            val intent =
                CropImage.activity(imageUri).setAspectRatio(160, 160).getIntent(requireContext())
            cropImage.launch(intent)
        }else{
            bottomSheetDialog?.dismiss()
        }
    }

    private val getImageCamera = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val intent =
                CropImage.activity(photoURI).setAspectRatio(160, 160).getIntent(requireContext())
            cropImage.launch(intent)
        }else{
            photoURI = null
            bottomSheetDialog?.dismiss()
        }
    }

    private val cropImage = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val result = CropImage.getActivityResult(it.data)
            photoURI = result.uri
            setImageView(photoURI!!)
        }
        bottomSheetDialog?.dismiss()
    }

    override val coroutineContext:CoroutineContext get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        job = Job()

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_registration, container, false
        )
        initializeView()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    private fun initializeView() {
        val datePickerDialogListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, date: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, date)
                val sdf = SimpleDateFormat("dd/MM/YYYY", Locale.US)
                binding.birthDate.editText?.setText(sdf.format(calendar.time))
                Log.d(TAG, "age is " + getAge())
                binding.institute.editText?.requestFocus()
            }

        binding.birthDate.editText?.setOnClickListener {
            DatePickerDialog(
                context ?: return@setOnClickListener,
                datePickerDialogListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.textButton.setOnClickListener {
            if(Utils.isOnline(requireContext())){
                registrationInterfaceModel = RegistrationModel(this)
                registrationInterfaceModel.registerUser()
            }else{
                showDialog("No Internet","Please check your internet connection and try again")
            }

        }

        binding.cameraClick.setOnClickListener {
            makeBottomSheet()
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.permission_required))
                    .setMessage(resources.getString(R.string.camera_reason))
                    .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        dialog.dismiss()
                    }
                    .show()
                bottomSheetDialog?.dismiss()
            }
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }

        }
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(context?.packageManager!!)?.also {
            val photoFile: File? = try {
                Utils.createImageFile()

            } catch (ex: IOException) {
                null
            }

            photoFile?.also {
                photoURI = FileProvider.getUriForFile(
                    requireContext(),
                    "com.ignitedminds.fileprovider", it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                getImageCamera.launch(intent)
            }
        }
    }

    private fun setImageView(uri: Uri) =
        Picasso.get().load(uri).fit().centerCrop().transform(CropCircleTransformation())
            .into(binding.profilePic)

    override fun showAlert() {
        val context = context ?: return
        MaterialAlertDialogBuilder(context)
            .setTitle("Exit")
            .setMessage("Are you sure you want to cancel registration ?")
            .setNegativeButton("No") { dialog, which ->
            }
            .setPositiveButton("Yes") { dialog, which ->
                activity?.finish()
            }
            .show()
    }

    private fun resetImageView() {
        binding.profilePic.setImageResource(R.drawable.profile_pic_icon)
        photoURI = null
        bottomSheetDialog?.dismiss()
    }

    private fun makeBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)

        val sheetView = LayoutInflater.from(context?.applicationContext)
            .inflate(
                R.layout.bottom_sheet_layout,
                bottomSheetDialog?.findViewById(R.id.bottom_sheet)
            )

        sheetView.findViewById<LinearLayout>(R.id.select_gallery).setOnClickListener {
            getImageGallery.launch(Intent(Intent.ACTION_GET_CONTENT).also { it.type ="image/*" })
        }

        sheetView.findViewById<LinearLayout>(R.id.select_camera).setOnClickListener {
            requestCameraPermission()
        }


        if(photoURI==null)
            sheetView.findViewById<LinearLayout>(R.id.select_remove).visibility = View.GONE

        sheetView.findViewById<LinearLayout>(R.id.select_remove).setOnClickListener {
            resetImageView()
        }


        bottomSheetDialog?.setContentView(sheetView)
        bottomSheetDialog?.show()
    }

    override fun getAge(): Int {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val days = calendar.get(Calendar.DAY_OF_MONTH)
        return Period.between(LocalDate.of(year, month, days), LocalDate.now()).years
    }

    override fun getPhoneNumber(): String {
        return SharedPrefsManager.getPhoneNumber(requireContext())!!
    }

    override fun getPhotoBitmap(): Bitmap? {
        return if(photoURI==null){
            null
        }else{
            if(Build.VERSION.SDK_INT<28){
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver,photoURI)
            }else{
                val source = ImageDecoder.createSource(requireContext().contentResolver,photoURI!!)
                ImageDecoder.decodeBitmap(source)
            }
        }
    }

    override fun showDialog(title: CharSequence, message: CharSequence) {
        hideProgressBar()
        Utils.showDialog(context, title, message)
    }

    override fun showProgressBar() = binding.linearProgressBar.setVisibility(View.VISIBLE)

    override fun hideProgressBar() = binding.linearProgressBar.setVisibility(View.GONE)

    override fun saveData() {
        SharedPrefsManager.apply {
            setFirstName(requireContext(),getFirstName())
            setLastName(requireContext(), getLastName())
            if(getMiddleName().isNotEmpty())
                setMiddleName(requireContext(),getMiddleName())
            setInstitute(requireContext(),getInstitutionName())
            setBirthDate(requireContext(),getBirthDate())
        }
    }

    override fun onSuccess(userModel: UserModel) {
        SharedPrefsManager.saveStringData(requireContext(),userModel)
        val action = RegistrationFragmentDirections.actionRegistrationFragmentToProfileFragment()
        if(getPhotoBitmap()==null){
            hideProgressBar()
            SharedPrefsManager.setLoginStatus(requireContext(),true)
            findNavController().navigate(action)
        }else{
            launch(IO) {
                SharedPrefsManager.saveStringData(requireContext(),userModel)
                val uri = Utils.writeImageToFileAsync(getPhotoBitmap()!!)
                if(uri?.path!=null){
                    SharedPrefsManager.setProfilePhotoPath(requireContext(),uri.path!!)
                    Log.d(TAG, "onSuccess: "+uri.path)
                }
                launch(Dispatchers.Main) {
                    hideProgressBar()
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun getFirstName(): String =  binding.firstName.editText?.text.toString()

    override fun getLastName(): String =  binding.lastName.editText?.text.toString()

    override fun getMiddleName(): String = binding.middleName.editText?.text.toString()

    override fun getInstitutionName(): String = binding.institute.editText?.text.toString()

    override fun getBirthDate(): String = binding.birthDate.editText?.text.toString()

    override fun setFirstNameError(error: String) = binding.firstName.setError(error)

    override fun setMiddleNameError(error: String) = binding.middleName.setError(error)

    override fun setLastNameError(error: String) = binding.lastName.setError(error)

    override fun setInstitutionError(error: String) = binding.institute.setError(error)

    override fun setBirthDateError(error: String) = binding.birthDate.setError(error)

}