package com.example.collart.MainPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.collart.Chat.AllChats.ChatsFragment
import com.example.collart.MainPage.Home.HomeFragment
import com.example.collart.NotificationsPage.NotificationFragment
import com.example.collart.PersonalPage.ProfileFragment
import com.example.collart.R
import com.example.collart.databinding.ActivityMainPageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainPageActivity : AppCompatActivity() {

    companion object{
        private var fragmentIndex = 1
    }

    private lateinit var binding: ActivityMainPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setItemIconTintList(null);


        val homeFragment = HomeFragment()

        val userPageFragment = ProfileFragment()

        val notificationFragment = NotificationFragment()

        val chatsFragment = ChatsFragment()

        if (fragmentIndex == 1) {
            setCurrentFragment(homeFragment)
        }
        if (fragmentIndex == 2) {
            setCurrentFragment(notificationFragment)
        }
        if (fragmentIndex == 3) {
            setCurrentFragment(chatsFragment)
        }
        if (fragmentIndex == 4) {
            setCurrentFragment(userPageFragment)
        }
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){

                R.id.home -> {
                    setCurrentFragment(homeFragment)
                    fragmentIndex = 1
                }

                R.id.messages -> {
                    setCurrentFragment(notificationFragment)
                    fragmentIndex = 2
                }

                R.id.chat -> {
                    setCurrentFragment(chatsFragment)
                    fragmentIndex = 3
                }

                R.id.personalPage -> {
                    setCurrentFragment(userPageFragment)
                    fragmentIndex = 4
                }

            }
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainPageFragment, fragment)
            commit()
        }
    }
}