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
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.viewpager2.widget.ViewPager2
import com.example.storyplayer.R
import com.example.storyplayer.animation.CubicPageTransformer
import com.example.storyplayer.data.StoryGroup
import com.example.storyplayer.data.StoryItem
import com.example.storyplayer.databinding.FragmentStoryBinding
import kotlin.math.abs


class StoryFragment : Fragment() {

    private lateinit var binding: FragmentStoryBinding
    private lateinit var stories: List<StoryGroup>
    private lateinit var adapter: StoryAdapter
    private lateinit var viewPager2: ViewPager2

    private var handler = Handler(Looper.getMainLooper())

    private lateinit var progressUpdater: Runnable

    private var currentGroupIndex = 0

    private lateinit var pageChangeCallback: ViewPager2.OnPageChangeCallback

    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarContainer: LinearLayout
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
        //progressBar = binding.storyProgressBar
        progressBarContainer = binding.progressBarContainer
        createProgressBars(stories[currentGroupIndex].storyItems.size)
        initProgressUpdaterRunnable()
        initTouchOverlay()
    }

    /**
     * Crate progress bars as story group's length
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun createProgressBars(storyCount: Int) {
        binding.progressBarContainer.removeAllViews() // Clear existing progress bars if any
        for (i in 0 until storyCount) {
            val progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f / storyCount) // Weighted equally
                progressDrawable = context?.getDrawable(R.drawable.story_progress_bar)
            }
            binding.progressBarContainer.addView(progressBar)
        }
    }

    /**
     * It init progress updater, it starts from 0 after that it will update it in every 50 msec
     * if it reaches the maximum, it slides for the next story
     */
    private fun initProgressUpdaterRunnable(){
        progressBar = binding.progressBarContainer.getChildAt(stories[currentGroupIndex].lastSeenStoryIndex) as ProgressBar
        progressBar.progress = 0
        initProgressUpdateInterval(currentGroupIndex,stories[currentGroupIndex].lastSeenStoryIndex)

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
     * It handles to progress bars
     */
    private fun handleProgressBars(storyIndex: Int) {
        progressBar = binding.progressBarContainer.getChildAt(storyIndex) as ProgressBar
        for (i in 0 until binding.progressBarContainer.childCount) {
            val progressBar = binding.progressBarContainer.getChildAt(i) as ProgressBar
            if (i < storyIndex) {
                progressBar.progress = progressBar.max // Mark previous stories as completed
            } else if (i == storyIndex) {
                progressBar.progress = 0 // Reset current story's progress bar
            } else {
                progressBar.progress = 0 // Reset future stories' progress bars
            }
        }
    }

    /**
     * It gives stories into the list
     */
    private fun initStories(): List<StoryGroup>{
        val firstStoryList = listOf(
            StoryItem(R.drawable.cat, false),
            StoryItem(R.raw.short_video, true),
            StoryItem( R.drawable.jph, false))
        val secondStoryList = listOf(
            StoryItem(R.drawable.golf, false),
            StoryItem( R.drawable.dag, false),
            StoryItem(R.raw.short_video, true))
        val thirdStoryList = listOf(
            StoryItem(R.raw.long_video, true),
            StoryItem(R.drawable.kahve, false),
            StoryItem( R.drawable.lamba, false))
        return listOf(
            StoryGroup(firstStoryList),
            StoryGroup(secondStoryList),
            StoryGroup(thirdStoryList))
    }

    /**
     * Updates the progress bar's interval with respect to image or the video files length
     */
    private fun initProgressUpdateInterval(groupIndex: Int,position: Int){
        progressUpdateInterval = if (stories[groupIndex].storyItems[position].isVideo){
            val videoLength = adapter.getVideoDuration(groupIndex,position,requireContext())
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
    private fun initTouchOverlay() {
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
                            moveToPreviousStoryGroup()
                        }
                        // when user tap left and slide right which is moving to the next story
                        else {
                            moveToNextStoryGroup()
                        }
                    } else{
                        val screenWidth = touchOverlay.width // Overlay'in genişliğini al
                        when {
                            startX < screenWidth * 0.2 -> moveToPreviousStory()
                            startX > screenWidth * 0.8 -> moveToNextStory()
                            else -> resumeStoryAndVideo()
                        }
                    }
                }
            }
            true // Consume the event
        }
    }

    /**
     * It moves to the next story group, by updating progress and calling its callback and
     * set current story as the next one
     */
    private fun moveToNextStoryGroup(){
        // if it is not the last story continue
        if (currentGroupIndex < adapter.itemCount - 1){

            val nextItem =  currentGroupIndex + 1
            createProgressBars(stories[nextItem].storyItems.size)
            handleProgressBars(stories[nextItem].lastSeenStoryIndex)
            // next story group
            initProgressUpdateInterval(nextItem, stories[nextItem].lastSeenStoryIndex)
            viewPager2.setCurrentItem(nextItem, true) // true for smooth scrolling
            currentGroupIndex = nextItem // Update the current page index
            // Reset progress bar for the next story
            progressBar.progress = 0
            handler.postDelayed(progressUpdater, progressUpdateInterval) // call it every 50msec
        } else{
            resumeStoryAndVideo()
            if (progressBar.progress == progressBar.max){
                pauseStoryAndVideo()
            }
        }
    }

    /**
     * It moves to the previous story group
     */
    private fun moveToPreviousStoryGroup() {
        // if it is not the first story continue
        if (currentGroupIndex > 0) {
            val prevItem = currentGroupIndex - 1
            // prev story group

            // handle progress bar
            createProgressBars(stories[prevItem].storyItems.size)
            handleProgressBars(stories[prevItem].lastSeenStoryIndex)

            initProgressUpdateInterval(prevItem, stories[prevItem].lastSeenStoryIndex)
            viewPager2.setCurrentItem(prevItem, true) // true for smooth scrolling
            currentGroupIndex = prevItem // Update the current page index
            // Reset progress bar for the previous story
            progressBar.progress = 0
            // if it's video resume
            val currentStoryGroup = stories[currentGroupIndex]
            if (currentStoryGroup.storyItems[currentStoryGroup.lastSeenStoryIndex].isVideo){
                adapter.resumeVideoAtPosition(currentGroupIndex)
            }
            handler.postDelayed(progressUpdater, progressUpdateInterval) // call it every 50msec
        } else{
            resumeStoryAndVideo()
        }
    }

    /**
     * It moves to the next story, by updating progress and calling its callback and
     * set current story as the next one
     */
    private fun moveToNextStory(){
        // if it is not the last story continue
        val currentStoryGroup = stories[currentGroupIndex]
        if (currentStoryGroup.lastSeenStoryIndex < currentStoryGroup.storyItems.size - 1){
            val nextStoryIndex =  currentStoryGroup.lastSeenStoryIndex + 1
            // handle progress bar
            handleProgressBars(nextStoryIndex)
            currentStoryGroup.lastSeenStoryIndex = nextStoryIndex //update storyIndex
            adapter.setLastSeenStoryIndex(currentGroupIndex,nextStoryIndex)
            // next story group
            initProgressUpdateInterval(currentGroupIndex, stories[currentGroupIndex].lastSeenStoryIndex)
            viewPager2.setCurrentItem(currentGroupIndex, true) // true for smooth scrolling
            // Reset progress bar for the next story
            progressBar.progress = 0
            handler.postDelayed(progressUpdater, progressUpdateInterval) // call it every 50msec
        } else{
            moveToNextStoryGroup()
        }
    }


    private fun moveToPreviousStory() {
        // if it is not the first story continue
        val currentStoryGroup = stories[currentGroupIndex]
        if (currentStoryGroup.lastSeenStoryIndex > 0) {
            val prevStoryIndex = currentStoryGroup.lastSeenStoryIndex - 1
            currentStoryGroup.lastSeenStoryIndex = prevStoryIndex

            // handle progress bar
            handleProgressBars(prevStoryIndex)
            adapter.setLastSeenStoryIndex(currentGroupIndex,prevStoryIndex)
            // prev story group
            initProgressUpdateInterval(currentGroupIndex, stories[currentGroupIndex].lastSeenStoryIndex)
            viewPager2.setCurrentItem(currentGroupIndex, true) // true for smooth scrolling
            // Reset progress bar for the previous story
            progressBar.progress = 0
            handler.postDelayed(progressUpdater, progressUpdateInterval) // call it every 50msec
        } else{
            moveToPreviousStoryGroup()
        }
    }

    private fun pauseStoryAndVideo() {
        Log.i("Story","Paused")
        val currentStoryGroup = stories[currentGroupIndex]
        if (currentStoryGroup.storyItems[currentStoryGroup.lastSeenStoryIndex].isVideo){
            adapter.pauseVideoAtPosition(currentGroupIndex)
        }
        handler.removeCallbacks(progressUpdater)
    }

    private fun resumeStoryAndVideo() {
        Log.i("Story","Resumed")
        val currentStoryGroup = stories[currentGroupIndex]
        if (currentStoryGroup.storyItems[currentStoryGroup.lastSeenStoryIndex].isVideo){
            adapter.resumeVideoAtPosition(currentGroupIndex)
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