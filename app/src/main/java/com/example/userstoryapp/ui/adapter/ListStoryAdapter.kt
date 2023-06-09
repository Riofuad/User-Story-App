package com.example.userstoryapp.ui.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.userstoryapp.ui.activities.DetailActivity
import com.example.userstoryapp.data.remote.response.Story
import com.example.userstoryapp.databinding.ItemRowStoryBinding

class ListStoryAdapter : PagingDataAdapter<Story, ListStoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    inner class ViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            with(binding) {
                tvItemName.text = story.name
                Glide.with(itemView)
                    .load(story.photoUrl)
                    .into(ivItemPhoto)
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_USER_NAME, story.name)
                    intent.putExtra(DetailActivity.EXTRA_IMAGE_URL, story.photoUrl)
                    intent.putExtra(DetailActivity.EXTRA_CONTENT_DESCRIPTION, story.description)
                    try {
                        intent.putExtra(DetailActivity.EXTRA_LATITUDE, story.lat.toString())
                        intent.putExtra(DetailActivity.EXTRA_LONGITUDE, story.lon.toString())
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(ivItemPhoto, "story_image"),
                            Pair(tvItemName, "story_name")
                        )

                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
        const val TAG = "StoryAdapter"

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}