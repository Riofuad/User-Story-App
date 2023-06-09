package com.example.userstoryapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.userstoryapp.R
import com.example.userstoryapp.data.remote.retrofit.ApiConfig
import com.example.userstoryapp.databinding.ActivityMainBinding
import com.example.userstoryapp.ui.adapter.ListStoryAdapter
import com.example.userstoryapp.ui.adapter.StoryLoadingStateAdapter
import com.example.userstoryapp.ui.viewmodels.StoryViewModelFactory
import com.example.userstoryapp.ui.viewmodels.SettingViewModel
import com.example.userstoryapp.ui.viewmodels.SettingViewModelFactory
import com.example.userstoryapp.ui.viewmodels.StoryPagerViewModel
import com.example.userstoryapp.utils.Helper
import com.example.userstoryapp.utils.SettingPreferences
import com.example.userstoryapp.utils.dataStore
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityMainBinding
    private var tempToken = ""
    private val preferences = SettingPreferences.getInstance(dataStore)
    private val viewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(preferences)
    }
    private val storyAdapter = ListStoryAdapter()
    private lateinit var newStory: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newStory =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                this.onRefresh()
            }

        binding.actionFab.shrink()
        setUpActionButton()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun getUserToken() = tempToken

    private fun getStoryViewModel(): StoryPagerViewModel {
        val viewModel: StoryPagerViewModel by viewModels {
            StoryViewModelFactory(
                this,
                ApiConfig.getApiService(),
                getUserToken()
            )
        }
        return viewModel
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionGranted()) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.error_camera_permission),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setUpActionButton() {
        viewModel.getUserPreferences(SettingPreferences.Companion.UserPreferences.UserToken.name)
            .observe(this) { token ->
                tempToken = "Bearer $token"
                val storyViewModel = getStoryViewModel()
                storyViewModel.story.observe(this) {
                    storyAdapter.submitData(lifecycle, it)

                }
                binding.rvListStory.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(context)
                    isNestedScrollingEnabled = false
                    adapter = storyAdapter.withLoadStateFooter(footer = StoryLoadingStateAdapter {
                        storyAdapter.retry()
                    })
                }
            }

        var isFabVisible = false
        binding.actionFab.setOnClickListener {
            if (!isFabVisible) {
                binding.fabAddStory.show()
                binding.fabMap.show()
                binding.fabChangeLanguage.show()
                binding.actionLogout.show()
                binding.fabAddStory.visibility = View.VISIBLE
                binding.fabMap.visibility = View.VISIBLE
                binding.fabChangeLanguage.visibility = View.VISIBLE
                binding.actionLogout.visibility = View.VISIBLE
                binding.actionFab.extend()
                isFabVisible = true
            } else {
                binding.fabAddStory.hide()
                binding.fabMap.hide()
                binding.fabChangeLanguage.hide()
                binding.actionLogout.hide()
                binding.fabAddStory.visibility = View.GONE
                binding.fabMap.visibility = View.GONE
                binding.fabChangeLanguage.visibility = View.GONE
                binding.actionLogout.visibility = View.GONE
                binding.actionFab.shrink()
                isFabVisible = false
            }
        }

        binding.fabAddStory.setOnClickListener {
            if (Helper.isPermissionGranted(this, Manifest.permission.CAMERA)) {
                val intent = Intent(this@MainActivity, CameraActivity::class.java)
                newStory.launch(intent)
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    REQUIRED_PERMISSION,
                    REQUEST_CODE_PERMISSION
                )
            }
        }

        binding.fabMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra(MapsActivity.EXTRA_TOKEN, getUserToken())
            startActivity(intent)
        }

        binding.fabChangeLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding.actionLogout.setOnClickListener {
            viewModel.clearUserPreferences()
            startActivity(Intent(this, AuthActivity::class.java))
        }

        binding.swipeRefresh.setOnRefreshListener {
            onRefresh()
        }
    }

    override fun onRefresh() {
        binding.swipeRefresh.isRefreshing = true
        storyAdapter.refresh()
        Timer().schedule(2000) {
            binding.swipeRefresh.isRefreshing = false
            binding.rvListStory.smoothScrollToPosition(0)
        }
    }

    companion object {
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }
}