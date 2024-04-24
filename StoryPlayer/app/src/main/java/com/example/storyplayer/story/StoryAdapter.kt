package com.example.storyplayer.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.storyplayer.R
import com.example.storyplayer.databinding.StoryItemBinding

class StoryAdapter (val stories: List<Int>): RecyclerView.Adapter<StoryAdapter.StoryAdapterViewHolder>() {

    inner class StoryAdapterViewHolder(val binding: StoryItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: StoryItemBinding = DataBindingUtil.inflate(inflater, R.layout.story_item, parent,false)
        return StoryAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return stories.size
    }

    override fun onBindViewHolder(holder: StoryAdapterViewHolder, position: Int) {
        val currentStory = stories[position]
        val binding = holder.binding
        binding.ivImageView.setImageResource(currentStory)
    }


}