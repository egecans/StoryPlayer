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
import com.example.storyplayer.databinding.FragmentStoryBinding


class StoryFragment : Fragment() {

    private lateinit var binding: FragmentStoryBinding
    private lateinit var images: List<Int>
    private lateinit var adapter: StoryAdapter
    private lateinit var viewPager2: ViewPager2

    private var handler = Handler(Looper.getMainLooper())

    private lateinit var progressUpdater: Runnable
    private var currentPageIndex = 0

    private lateinit var pageChangeCallback: ViewPager2.OnPageChangeCallback

    private lateinit var progressBar: ProgressBar
    private var currentProgress = 0
    private val progressUpdateInterval: Long = 50 // Update the progress bar every 50 milliseconds

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
        Log.i("initItems","Entered")
        images = initImages()
        viewPager2 = binding.viewPager
        adapter = StoryAdapter(images)
        viewPager2.adapter = adapter
        progressBar = binding.storyProgressBar
        initProgressUpdaterRunnable(0)
        initTouchOverlay()
    }

    /**
     * It init progress updater, it starts from 0 after that it will update it in every 50 msec
     * if it reaches the maximum, it slides for the next story
     */
    private fun initProgressUpdaterRunnable(initProgress: Int){
        progressBar.progress = initProgress
        Log.i("initProgressUpdaterRunnable","Entered")
        progressUpdater = object : Runnable {
            override fun run() {
                Log.i("progressUpdater","Entered")
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
            viewPager2.setCurrentItem(nextItem, true) // true for smooth scrolling
            currentPageIndex = nextItem // Update the current page index
            // Reset progress bar for the next story
            progressBar.progress = 0
            handler.postDelayed(progressUpdater, progressUpdateInterval) // call it every 50msec
        }
    }

    /**
     * It gives stories into the list
     */
    private fun initImages(): List<Int>{
        return listOf(
            R.drawable.cat,
            R.drawable.jph,
            R.drawable.kelebek
        )
    }

    /**
     * This is invisible frame, it understand when user holds on the screen for pausing,
     * when user clicks the screen it pauses the story, else it continue
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initTouchOverlay(){
        val touchOverlay = binding.touchOverlay
        touchOverlay.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pauseStory()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> resumeStory()
            }
            true // Consume the event
        }
    }

    private fun pauseStory() {
        Log.i("Story","Paused")
        handler.removeCallbacks(progressUpdater)
    }

    private fun resumeStory() {
        Log.i("Story","Resumed")
        handler.postDelayed(progressUpdater, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("onDestroyView","Enter")
        viewPager2.unregisterOnPageChangeCallback(pageChangeCallback)
        handler.removeCallbacks(progressUpdater) // Clean up the handler
    }

    /**
     * It inits slider, as it has next object it will slide to the next in 5 sec
     * and updates progressUpdater in every 50 msec

    private fun initSlider() {
    Log.i("initSlider","Entered")
    slideRunnable = Runnable {
    Log.i("slideRunnable","Entered")
    val nextItem = if (currentPageIndex < adapter.itemCount - 1) currentPageIndex + 1 else 0
    viewPager2.setCurrentItem(nextItem, true) // true for smooth scrolling
    currentPageIndex = nextItem // Update the current page index
    // Reset progress bar for the next story
    progressBar.progress = 0
    // Restart the progress updater for the new story
    handler.removeCallbacks(progressUpdater)
    handler.postDelayed(progressUpdater, progressUpdateInterval)
    }
    }
     */


    /**
     * Here is the progressbar's function, it will update the progressbar until it reaches the maximum (%100)

    private fun startProgress() {
        Log.i("startProgress","Entered")
        progressBar.progress = 0
        val progressRunnable = object : Runnable {
            override fun run() {
                if (progressBar.progress < progressBar.max) {
                    progressBar.incrementProgressBy(1) // Increment progress
                    handler.postDelayed(this, progressUpdateInterval)
                } else {
                    moveToNextStory()
                }
            }
        }
        handler.postDelayed(progressRunnable, progressUpdateInterval)
    }


    private fun moveToNextStory() {
        Log.i("moveToNextStory","Entered")
        val nextItem = (viewPager2.currentItem + 1) % adapter.itemCount
        viewPager2.currentItem = nextItem
    }
     */
    /**
     * This initializes view pager's callback function. It starts  the progess

    private fun setupViewPagerCallback() {
        Log.i("setupViewPagerCallback","Enter")
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.i("onPageSelected","Enter")
                super.onPageSelected(position)
                handler.removeCallbacksAndMessages(null) // Clear all existing callbacks
                val initProgress = if (progressBar.progress == progressBar.max) 0 else progressBar.progress
                initProgressUpdaterRunnable(initProgress) // Start the progress for the new page
            }
        })
    }
     */

}