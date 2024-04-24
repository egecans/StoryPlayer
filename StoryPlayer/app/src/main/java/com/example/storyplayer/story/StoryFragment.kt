package com.example.storyplayer.story

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.viewpager2.widget.ViewPager2
import com.example.storyplayer.R
import com.example.storyplayer.animation.CubicPageTransformer
import com.example.storyplayer.data.StoryItem
import com.example.storyplayer.databinding.FragmentStoryBinding
import kotlin.math.abs


class StoryFragment : Fragment() {

    private lateinit var binding: FragmentStoryBinding
    private lateinit var stories: List<StoryItem>
    private lateinit var adapter: StoryAdapter
    private lateinit var viewPager2: ViewPager2

    private var handler = Handler(Looper.getMainLooper())

    private lateinit var progressUpdater: Runnable
    private var currentPageIndex = 0

    private lateinit var pageChangeCallback: ViewPager2.OnPageChangeCallback

    private lateinit var progressBar: ProgressBar
    private var progressUpdateInterval: Long = 50 // Update the progress bar every 50 milliseconds

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initItems()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * It calls initialization functions that will called in onViewCreated to make our class more tidy
     */
    private fun initItems(){
        stories = initStories()
        viewPager2 = binding.viewPager
        adapter = StoryAdapter(stories)
        viewPager2.adapter = adapter
        viewPager2.setPageTransformer(CubicPageTransformer())
        progressBar = binding.storyProgressBar
        initProgressUpdaterRunnable(0)
        initTouchOverlay2()
    }

    /**
     * It init progress updater, it starts from 0 after that it will update it in every 50 msec
     * if it reaches the maximum, it slides for the next story
     */
    private fun initProgressUpdaterRunnable(initProgress: Int){
        progressBar.progress = initProgress
        initProgressUpdateInterval(currentPageIndex)

        progressUpdater = object : Runnable {
            override fun run() {
                if (progressBar.progress < progressBar.max) {
                    progressBar.incrementProgressBy(1) // Increment progress
                    handler.postDelayed(this, progressUpdateInterval) // call it every 50msec
                }
                // when it reaches its max, moves for the next story
                else {
                    moveToNextStory()
                }
            }
        }
        handler.postDelayed(progressUpdater, progressUpdateInterval) // Call this function after initialization
    }

    /**
     * It moves to the next story, by updating progress and calling its callback and
     * set current story as the next one
     */
    private fun moveToNextStory(){
        // if it is not the last story continue
        if (currentPageIndex < adapter.itemCount - 1){

            val nextItem =  currentPageIndex + 1
            initProgressUpdateInterval(nextItem)
            viewPager2.setCurrentItem(nextItem, true) // true for smooth scrolling
            currentPageIndex = nextItem // Update the current page index
            // Reset progress bar for the next story
            progressBar.progress = 0
            handler.postDelayed(progressUpdater, progressUpdateInterval) // call it every 50msec
        } else{
            resumeStoryAndVideo()
        }
    }

    /**
     * It gives stories into the list
     */
    private fun initStories(): List<StoryItem>{
        return listOf(
            StoryItem(R.drawable.cat, false),
            StoryItem( R.drawable.jph, false),
            StoryItem(R.raw.short_video, true),
            StoryItem( R.drawable.kelebek, false),
            StoryItem(R.raw.long_video, true),
        )
    }

    /**
     * Updates the progress bar's interval with respect to image or the video files length
     */
    private fun initProgressUpdateInterval(position: Int){
        progressUpdateInterval = if (stories[position].isVideo){
            val videoLength = adapter.getVideoDuration(position,requireContext())
            Log.i("VideoLength: ", videoLength.toString())
            videoLength/100
        } else{
            50
        }

    }


    private var startX: Float = 0f // Variable to store the initial touch position
    private val minSwipeDistance = 30f

    /**
     * This is invisible frame, it understand when user holds on the screen for pausing,
     * when user clicks the screen it pauses the story, else it continue
     * It slides moves to the previous story when slide left, next story when slide right
     */
    @SuppressLint("ClickableViewAccessibility")

    private fun initTouchOverlay2() {
        val touchOverlay = binding.touchOverlay
        touchOverlay.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x // Store the initial touch position
                    pauseStoryAndVideo()
                }
                MotionEvent.ACTION_UP -> {
                    val diffX = event.x - startX
                    // calculate the difference between when user rest and hold
                    if (abs(diffX) > minSwipeDistance) {
                        // when user tap right and slide left which is moving back
                        if (diffX > 0) {
                            moveToPreviousStory()
                        }
                        // when user tap left and slide right which is moving to the next story
                        else {
                            moveToNextStory()
                        }
                    } else{
                        resumeStoryAndVideo()
                    }
                }
            }
            true // Consume the event
        }
    }

    /**
     * It moves to the previous story
     */
    private fun moveToPreviousStory() {
        // if it is not the first story continue
        if (currentPageIndex > 0) {
            val prevItem = currentPageIndex - 1
            initProgressUpdateInterval(prevItem)
            viewPager2.setCurrentItem(prevItem, true) // true for smooth scrolling
            currentPageIndex = prevItem // Update the current page index
            // Reset progress bar for the previous story
            progressBar.progress = 0
            handler.postDelayed(progressUpdater, progressUpdateInterval) // call it every 50msec
        } else{
            resumeStoryAndVideo()
        }
    }

    private fun pauseStory() {
        Log.i("Story","Paused")
        handler.removeCallbacks(progressUpdater)
    }

    private fun pauseStoryAndVideo() {
        Log.i("Story","Paused")
        if (stories[currentPageIndex].isVideo){
            adapter.pauseVideoAtPosition(currentPageIndex)
        }
        handler.removeCallbacks(progressUpdater)
    }

    private fun resumeStoryAndVideo() {
        Log.i("Story","Resumed")
        if (stories[currentPageIndex].isVideo){
            adapter.resumeVideoAtPosition(currentPageIndex)
        }
        handler.postDelayed(progressUpdater, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("onDestroyView","Enter")
        viewPager2.unregisterOnPageChangeCallback(pageChangeCallback)
        handler.removeCallbacks(progressUpdater) // Clean up the handler
    }
}