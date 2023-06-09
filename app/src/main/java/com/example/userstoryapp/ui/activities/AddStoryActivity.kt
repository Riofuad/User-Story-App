package com.example.userstoryapp.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.userstoryapp.R
import com.example.userstoryapp.databinding.ActivityAddStoryBinding
import com.example.userstoryapp.ui.viewmodels.SettingViewModel
import com.example.userstoryapp.ui.viewmodels.SettingViewModelFactory
import com.example.userstoryapp.ui.viewmodels.StoryViewModel
import com.example.userstoryapp.utils.Helper
import com.example.userstoryapp.utils.SettingPreferences
import com.example.userstoryapp.utils.dataStore
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var userToken: String? = null
    private val storyViewModel: StoryViewModel by viewModels()
    private var isPicked: Boolean? = false
    private var getFile: File? = null
    private var getResult: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.let { res ->
                    isPicked = res.getBooleanExtra(EXTRA_PICK_LOCATION, false)
                    storyViewModel.isLocationPicked.postValue(isPicked)
                    val lat = res.getDoubleExtra(
                        EXTRA_LATITUDE,
                        0.0
                    )
                    val lon = res.getDoubleExtra(
                        EXTRA_LONGITUDE,
                        0.0
                    )
                    binding.tvLocation.text = Helper.parseAddressLocation(this, lat, lon)
                    storyViewModel.lat.postValue(lat)
                    storyViewModel.lon.postValue(lon)
                }
            }
        }

        getToken()
        setStoryData()
        observe()
    }

    private fun setStoryData() {
        val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getSerializableExtra(EXTRA_PHOTO_RESULT, File::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getSerializableExtra(EXTRA_PHOTO_RESULT)
        } as File

        val isBackCamera = intent?.getBooleanExtra(EXTRA_CAMERA_MODE, true) as Boolean

        myFile.let { file ->
            Helper.rotateFile(file, isBackCamera)
            getFile = file
            binding.ivItemPhoto.setImageBitmap(BitmapFactory.decodeFile(file.path))
        }

        binding.apply {
            btnSelectLocation.setOnClickListener {
                if (Helper.isPermissionGranted(
                        this@AddStoryActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    val intentPickLocation =
                        Intent(this@AddStoryActivity, AddStoryPickLocationActivity::class.java)
                    getResult?.launch(intentPickLocation)
                } else {
                    ActivityCompat.requestPermissions(
                        this@AddStoryActivity,
                        REQUIRED_PERMISSION,
                        LOCATION_PERMISSION_CODE
                    )
                }
            }

            btnClearLocation.setOnClickListener {
                storyViewModel.isLocationPicked.postValue(false)
            }

            buttonAdd.setOnClickListener {
                if (binding.edAddDescription.text.isNotEmpty()) {
                    uploadStory(myFile, binding.edAddDescription.text.toString())
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        getString(R.string.validation_empty_story_description),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getToken() {
        val preferences = SettingPreferences.getInstance(dataStore)
        val settingViewModel = ViewModelProvider(
            this,
            SettingViewModelFactory(preferences)
        )[SettingViewModel::class.java]
        settingViewModel.getUserPreferences(SettingPreferences.Companion.UserPreferences.UserToken.name)
            .observe(this) { token ->
                userToken = StringBuilder("Bearer ").append(token).toString()
            }
    }

    private fun observe() {
        storyViewModel.let { viewModel ->
            viewModel.isSuccessUploadStory.observe(this) {
                if (it) {
                    Toast.makeText(
                        this,
                        getString(R.string.successful_upload_story),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
            viewModel.isLoading.observe(this) {
                binding.progressBar.visibility = it
            }
            viewModel.error.observe(this) {
                if (it.isNotEmpty()) {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            }
            viewModel.isLocationPicked.observe(this) {
                binding.tvLocation.isVisible = it
                binding.btnClearLocation.isVisible = it
                binding.btnSelectLocation.isVisible = !it
            }
        }
    }


    private fun uploadStory(image: File, description: String) {
        if (userToken != null) {
            if (storyViewModel.isLocationPicked.value != true) {
                storyViewModel.uploadStory(this, userToken!!, image, description)
            } else {
                storyViewModel.uploadStory(
                    this,
                    userToken!!,
                    image,
                    description,
                    true,
                    storyViewModel.lat.value.toString(),
                    storyViewModel.lon.value.toString()
                )
            }
        } else {
            Toast.makeText(this, getString(R.string.error_header_token), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_PHOTO_RESULT = "PHOTO_RESULT"
        const val EXTRA_CAMERA_MODE = "CAMERA_MODE"
        const val EXTRA_PICK_LOCATION = "PICK_LOCATION"
        const val EXTRA_LATITUDE = "LATITUDE"
        const val EXTRA_LONGITUDE = "LONGITUDE"
        const val LOCATION_PERMISSION_CODE = 30
        private val REQUIRED_PERMISSION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}