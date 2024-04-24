package com.example.storyplayer.story

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.viewpager2.widget.ViewPager2
import com.example.storyplayer.R
import com.example.storyplayer.databinding.FragmentStoryBinding


class StoryFragment : Fragment() {

    private lateinit var binding: FragmentStoryBinding
    lateinit var images: List<Int>
    lateinit var adapter: StoryAdapter
    private lateinit var viewPager2: ViewPager2

    private var handler = Handler(Looper.getMainLooper())
    private lateinit var slideRunnable: Runnable

    private lateinit var progressUpdater: Runnable
    private var currentPageIndex = 0

    private lateinit var pageChangeCallback: ViewPager2.OnPageChangeCallback

    private lateinit var progressBar: ProgressBar
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
        images = initImages()
        viewPager2 = binding.viewPager
        adapter = StoryAdapter(images)
        progressBar = binding.storyProgressBar
        initViewPager()
        initSlider()

    }


    /**
     * It inits slider, as it has next object it will slide to the next in 5 sec
     */
    private fun initSlider() {
        slideRunnable = Runnable {
            val nextItem = if (currentPageIndex < adapter.itemCount - 1) currentPageIndex + 1 else 0
            viewPager2.setCurrentItem(nextItem, true) // true for smooth scrolling
            handler.postDelayed(slideRunnable, 5000) // post again after 5 seconds
        }
        progressUpdater = Runnable {
            handler.postDelayed(progressUpdater, 50) // Update progress every 50 milliseconds
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
     * It initializes viewPager for dragging automatically
     */
    private fun initViewPager(){
        viewPager2.adapter = adapter
        setupViewPagerCallback()
    }


    private fun startProgress() {
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
        val nextItem = (viewPager2.currentItem + 1) % adapter.itemCount
        viewPager2.currentItem = nextItem
    }

    private fun setupViewPagerCallback() {
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacksAndMessages(null) // Clear all existing callbacks
                startProgress() // Start the progress for the new page
            }
        })
    }


    override fun onResume() {
        super.onResume()
        handler.postDelayed(slideRunnable, 5000) // Start sliding after a delay
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(slideRunnable) // Stop sliding when the fragment is not visible
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager2.unregisterOnPageChangeCallback(pageChangeCallback)
        handler.removeCallbacks(slideRunnable)
        handler.removeCallbacks(progressUpdater) // Clean up the handler
    }

}