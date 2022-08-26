package com.fictivestudios.lakoda.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.adapters.VideosAdapter
import com.fictivestudios.lakoda.model.VideoItem
import kotlinx.android.synthetic.main.activity_tiktok.*


class TiktokActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tiktok)


        val videoItems = ArrayList<VideoItem>()

        videoItems.add(
            VideoItem("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4",
            "Women In Tech",
            "International Women's Day 2019"))

        videoItems.add(
            VideoItem("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                "Happy Hour Wednesday",
                "Depth-First Search Algorithm"))

        videoItems.add(
            VideoItem("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
                "Sasha Solomon",
                "How Sasha Solomon Became a Software Developer at Twitter"))


        //viewPagerVideos.setAdapter( VideosAdapter(videoItems, requireContext(), this));

    }
}