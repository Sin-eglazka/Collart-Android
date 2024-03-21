package com.example.collart.OnBoarding

import OnboardingAdapter
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.collart.MainActivity
import com.example.collart.R
import com.example.collart.databinding.ActivityOnboardingBinding


data class OnboardingItem(
    val title: String,
    val description: String
)

class OnboardingActivity : AppCompatActivity() {

    private var currentPage = 0
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val onboardingItems = listOf(
            OnboardingItem(
                "Добро пожаловать в Collart!",
                ""
            ),
            OnboardingItem(
                "Находите специалистов для коллабораций",
                "Благодаря возможностям поиска и приглашения специалистов в свои проекты"
            ),
            OnboardingItem(
                "Создавайте личное портфолио",
                "Загружайте свои проекты, чтобы другие пользователи приглашали Вас в коллаборации"
            )
        )

        val adapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = adapter
        val circles = binding.circles
        val circleActive = ContextCompat.getDrawable(this, R.drawable.circle_active)
        val circleInactive = ContextCompat.getDrawable(this, R.drawable.circle_inactive)

        binding.btnNext.setOnClickListener {
            if (currentPage < onboardingItems.size - 1) {
                currentPage++
                for (i in 0 until circles.childCount) {
                    val child = circles.getChildAt(i)
                    child.background = circleInactive
                }
                circles.getChildAt(currentPage).background = circleActive
                binding.viewPager.setCurrentItem(currentPage, true)

            } else {
                // Navigate to the main activity or next screen
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        binding.btnSkip.setOnClickListener {
            // Navigate to the main activity or next screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}