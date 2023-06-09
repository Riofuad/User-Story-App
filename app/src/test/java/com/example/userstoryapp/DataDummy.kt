package com.example.userstoryapp

import com.example.userstoryapp.data.remote.response.Story

object DataDummy {
    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story =  Story(
                "story-$i",
                "https://story-api.dicoding.dev/images/stories/photos-1682913993162_Kh6eZ_AQ.jpg",
                "2023-04-30T10:00:00.{$i}Z",
                "name + $i",
                "description $i",
                null,
                null
            )
            items.add(story)
        }
        return items
    }
}