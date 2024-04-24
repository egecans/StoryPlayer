package com.example.storyplayer.animation

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class CubicPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.apply {
            // Set pivot points for cubic transformation
            pivotX = width / 2f
            pivotY = height / 2f

            // Apply cubic transformation based on the position
            when {
                position < -1 -> {
                    // Page is off-screen to the left
                    alpha = 0f
                }
                position <= 1 -> {
                    // Page is visible on the screen
                    alpha = 1f

                    // Apply cubic transformation
                    val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // Adjust translation based on the direction of the slide
                    if (position < 0) {
                        // Sliding to the left (move to next story)
                        translationX = -position * (width + pageMargin)
                    } else {
                        // Sliding to the right (move to previous story)
                        translationX = position * (width + pageMargin)
                    }
                }
                else -> {
                    // Page is off-screen to the right
                    alpha = 0f
                }
            }
        }
    }

    companion object {
        private const val MIN_SCALE = 0.8f
        private const val pageMargin = 16 // Adjust the value as needed
    }
}


