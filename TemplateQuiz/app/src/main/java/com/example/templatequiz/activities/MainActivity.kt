package com.example.templatequiz.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.templatequiz.R
import com.example.templatequiz.additional.AnswerState
import com.example.templatequiz.databinding.ActivityMainBinding
import com.example.templatequiz.fragments.HomeFragment
import com.example.templatequiz.fragments.InfoFragment
import com.example.templatequiz.fragments.QuizFragment
import com.example.templatequiz.fragments.ResultFragment
import com.example.templatequiz.fragments.SplashFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = sb.top, bottom = sb.bottom)
            insets
        }

        setContentView(binding.root)

        splash()
        Handler(Looper.getMainLooper()).postDelayed({
            home()
        }, 1500)
    }

    /**
     * Открытие экрана с результатами квиза.
     *
     * @param correct  – количество правильных ответов
     * @param wrong    – количество неправильных
     * @param skipped  – количество пропущенных
     * @param total    – всего вопросов
     * @param states   – список состояний по каждому вопросу
     */
    fun result(
        correct: Int,
        wrong: Int,
        skipped: Int,
        total: Int,
        states: List<AnswerState>
    ) {
        val fragment = ResultFragment.newInstance(
            correct = correct,
            wrong = wrong,
            skipped = skipped,
            total = total,
            answerStates = ArrayList(states)   // newInstance ждёт ArrayList
        )
        changeFragment(fragment)
    }

    fun splash() {
        changeFragment(SplashFragment())
    }

    fun home() {
        changeFragment(HomeFragment())
    }

    fun info() {
        changeFragment(InfoFragment())
    }

    fun quiz() {
        changeFragment(QuizFragment())
    }

    private fun changeFragment(fragment: Fragment) {
        if (currentFragment != fragment) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_out_right)
            transaction.replace(R.id.frameContainer, fragment)
            transaction.commit()

            currentFragment = fragment
        }
    }
}