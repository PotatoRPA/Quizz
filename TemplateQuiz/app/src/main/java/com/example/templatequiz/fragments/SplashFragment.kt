package com.example.templatequiz.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.templatequiz.R
import com.example.templatequiz.databinding.FragmentSplashBinding

class SplashFragment: Fragment() {
    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProgressBar()
        setupTitle()
    }

    private fun setupProgressBar() {
        val progressBar = binding.splashProgress
        progressBar.max = 100

        ValueAnimator.ofInt(0, 100).apply {
            duration = 1500L
            addUpdateListener { animator ->
                progressBar.progress = animator.animatedValue as Int
            }
            start()
        }
    }

    private fun setupTitle() {
        val tv = binding.splashCenterText
        val text = tv.text.toString()

        val parts = text.split(" ")
        if (parts.size < 2) return

        val firstWord   = parts[0]
        val secondStart = text.indexOf(parts[1])

        val leftColor   = ContextCompat.getColor(requireContext(), R.color.title_left_text_color)
        val rightColor  = ContextCompat.getColor(requireContext(), R.color.title_right_text_color)

        val span = SpannableString(text).apply {
            setSpan(
                ForegroundColorSpan(leftColor),
                0,
                firstWord.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(rightColor),
                secondStart,
                text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        tv.text = span
    }
}