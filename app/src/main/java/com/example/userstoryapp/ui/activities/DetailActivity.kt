package com.example.userstoryapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.userstoryapp.databinding.ActivityDetailBinding
import com.example.userstoryapp.utils.Helper

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setStoryData()
    }

    private fun setStoryData() {
        binding.tvDetailName.text = intent.getStringExtra(EXTRA_USER_NAME)
        Glide.with(binding.root)
            .load(intent.getStringExtra(EXTRA_IMAGE_URL))
            .into(binding.ivDetailPhoto)
        binding.tvDetailDescription.text = intent.getStringExtra(EXTRA_CONTENT_DESCRIPTION)
        try {
            val lat = intent.getStringExtra(EXTRA_LATITUDE).toString()
            val lon = intent.getStringExtra(EXTRA_LONGITUDE).toString()
            binding.tvLocation.text = Helper.parseAddressLocation(this, lat.toDouble(), lon.toDouble())
            binding.tvLocation.isVisible = true
        } catch (e: Exception) {
            binding.tvLocation.isVisible = false
        }
    }

    companion object {
        const val EXTRA_USER_NAME = "extra_user_name"
        const val EXTRA_IMAGE_URL = "extra_image_url"
        const val EXTRA_CONTENT_DESCRIPTION = "extra_content_description"
        const val EXTRA_LATITUDE = "extra_latitude"
        const val EXTRA_LONGITUDE = "extra_longitude"
    }
}