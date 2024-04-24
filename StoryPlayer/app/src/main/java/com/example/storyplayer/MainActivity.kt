package com.example.storyplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.storyplayer.story.StoryFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(StoryFragment())
    }


    /**
     * This is for replacing the fragment without adding it to the stack
     */
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment) //replacing fragment
            commit() //call signals to the FragmentManager that all operations have been added to the transaction
        }
    }

    /** This adds fragments to the back stack, in this way user can return this fragment after the view has been changed.
     * and replace fragment.
     */
    fun addFragment(fragment: Fragment) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment)
        ft.addToBackStack(null)
        ft.commit()
    }

    /**
     * This pops the fragment from fragment stack
     */
    fun popFragment() {
        supportFragmentManager.popBackStack()
    }

}