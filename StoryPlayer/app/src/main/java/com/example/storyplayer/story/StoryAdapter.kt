package com.example.storyplayer.story

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.storyplayer.R
import com.example.storyplayer.data.StoryGroup
import com.example.storyplayer.data.StoryItem
import com.example.storyplayer.databinding.StoryItemBinding

class StoryAdapter (val stories: List<StoryGroup>): RecyclerView.Adapter<StoryAdapter.StoryAdapterViewHolder>() {

    inner class StoryAdapterViewHolder(val binding: StoryItemBinding): RecyclerView.ViewHolder(binding.root)


    private lateinit var recyclerView: RecyclerView


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: StoryItemBinding = DataBindingUtil.inflate(inflater, R.layout.story_item, parent,false)
        return StoryAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return stories.size
    }

    override fun onBindViewHolder(holder: StoryAdapterViewHolder, position: Int) {
        val currentStoryGroup = stories[position]
        val currentStory = currentStoryGroup.storyItems[currentStoryGroup.lastSeenStoryIndex]
        val binding = holder.binding
        if (currentStory.isVideo) {
            // Display VideoView for video story
            binding.videoView.visibility = View.VISIBLE
            binding.ivImageView.visibility = View.INVISIBLE
            binding.ivImageView.setImageDrawable(null)
            val videoUri = Uri.parse("android.resource://" + holder.itemView.context.packageName + "/" + currentStory.resourceId)
            binding.videoView.setVideoURI(videoUri)
            binding.videoView.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.start()
            }
            binding.videoView.setOnErrorListener { _, _, _ ->
                Log.e("VideoView", "Error playing video")
                true
            }
        } else {
            // Display ImageView for image story
            binding.ivImageView.visibility = View.VISIBLE
            binding.videoView.visibility = View.INVISIBLE
            binding.ivImageView.setImageResource(currentStory.resourceId)
        }
    }

    /**
     * Set last seen story index in the adapter also
     */
    fun setLastSeenStoryIndex(groupPosition: Int, storyIndex: Int) {
        if (groupPosition < stories.size) {
            val storyGroup = stories[groupPosition]
            storyGroup.lastSeenStoryIndex = storyIndex
            storyGroup.storyItems.forEachIndexed { index, storyItem ->
                storyItem.seenYet = index <= storyIndex
            }
            notifyItemChanged(groupPosition)
        }
    }


    fun getVideoDuration(groupIndex: Int,position: Int, context: Context): Long {
        return if (stories[groupIndex].storyItems[position].isVideo) {
            // Retrieve and return the video duration
            val videoUri = Uri.parse("android.resource://" + context.packageName + "/" + stories[groupIndex].storyItems[position].resourceId)
            MediaPlayer.create(context, videoUri)?.duration?.toLong() ?: 0L
        } else {
            // Default duration for images
            5000L
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    fun pauseVideoAtPosition(position: Int) {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) as? StoryAdapterViewHolder
        viewHolder?.binding?.videoView?.pause()
    }

    fun resumeVideoAtPosition(position: Int) {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) as? StoryAdapterViewHolder
        viewHolder?.binding?.videoView?.start()
    }


}