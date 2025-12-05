package com.example.templatequiz.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.templatequiz.R
import com.example.templatequiz.activities.MainActivity
import com.example.templatequiz.additional.AnswerState
import com.example.templatequiz.databinding.FragmentQuizBinding
import com.example.templatequiz.models.Question
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class QuizFragment : Fragment() {

    private lateinit var binding: FragmentQuizBinding

    private var questions: List<Question> = emptyList()
    private lateinit var answerStates: MutableList<AnswerState>

    private var currentIndex = 0
    private var correctCount = 0
    private var wrongCount = 0
    private var skippedCount = 0

    private var answered = false
    private var quizFinished = false

    // таймер на вопрос
    private val questionTimeMillis = 30_000L
    private var timer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIv.setOnClickListener {
            (requireActivity() as MainActivity).home()
        }

        questions = loadQuestionsFromAssets()

        if (questions.isNotEmpty()) {
            startQuiz()
        }

        // Клики по ответам
        binding.btnQuizAnswer1.setOnClickListener { onAnswerClick(0, binding.btnQuizAnswer1) }
        binding.btnQuizAnswer2.setOnClickListener { onAnswerClick(1, binding.btnQuizAnswer2) }
        binding.btnQuizAnswer3.setOnClickListener { onAnswerClick(2, binding.btnQuizAnswer3) }
        binding.btnQuizAnswer4.setOnClickListener { onAnswerClick(3, binding.btnQuizAnswer4) }

        // Skip / Try again
        binding.btnSkipOrTryAgain.setOnClickListener {
            if (quizFinished) {
                restartQuiz()
            } else {
                skipQuestion()
            }
        }

        // Next
        binding.btnStartQuiz.setOnClickListener {
            if (answered && !quizFinished) {
                goToNextQuestion()
            }
        }
    }

    private fun loadQuestionsFromAssets(): List<Question> {
        return try {
            val json = requireContext().assets.open("questions.json")
                .bufferedReader().use { it.readText() }

            val listType = object : TypeToken<List<Question>>() {}.type
            Gson().fromJson(json, listType)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun startQuiz() {
        quizFinished = false
        currentIndex = 0

        correctCount = 0
        wrongCount = 0
        skippedCount = 0

        answerStates = MutableList(questions.size) { AnswerState.DEFAULT }  // все пока не отвечены
        binding.btnSkipOrTryAgain.text = getString(R.string.skip)
        bindQuestion()
    }

    private fun restartQuiz() {
        timer?.cancel()
        startQuiz()
    }

    private fun skipQuestion() {
        timer?.cancel()

        // считаем пропуск только если вопрос ещё не был отвечен
        if (!answered && !quizFinished) {
            skippedCount++
            // в полоске результатов пропущенный будет серым (DEFAULT)
            answerStates[currentIndex] = AnswerState.DEFAULT
        }

        if (currentIndex < questions.lastIndex) {
            currentIndex++
            bindQuestion()
        } else {
            finishQuiz()
        }
    }

    private fun goToNextQuestion() {
        timer?.cancel()
        if (currentIndex < questions.lastIndex) {
            currentIndex++
            bindQuestion()
        } else {
            finishQuiz()
        }
    }

    private fun bindQuestion() {
        if (questions.isEmpty()) return

        val q = questions[currentIndex]
        answered = false

        loadQuestionImage(q.id)

        with(binding) {
            tvQuestion.text = q.question
            btnQuizAnswer1.text = q.answers.getOrNull(0) ?: ""
            btnQuizAnswer2.text = q.answers.getOrNull(1) ?: ""
            btnQuizAnswer3.text = q.answers.getOrNull(2) ?: ""
            btnQuizAnswer4.text = q.answers.getOrNull(3) ?: ""

            tvQuestionCounter.text = "${currentIndex + 1}/${questions.size}"
            quizProgress.max = questions.size
            quizProgress.progress = currentIndex + 1

            btnQuizAnswer1.isEnabled = true
            btnQuizAnswer2.isEnabled = true
            btnQuizAnswer3.isEnabled = true
            btnQuizAnswer4.isEnabled = true

            btnQuizAnswer1.setAnswerState(AnswerState.DEFAULT)
            btnQuizAnswer2.setAnswerState(AnswerState.DEFAULT)
            btnQuizAnswer3.setAnswerState(AnswerState.DEFAULT)
            btnQuizAnswer4.setAnswerState(AnswerState.DEFAULT)

            btnStartQuiz.isEnabled = false
            btnStartQuiz.alpha = 0.5f

            tvTimer.text = formatTime(questionTimeMillis)
        }

        startTimer()
    }

    private fun loadQuestionImage(id: Int) {
        val assetManager = requireContext().assets
        val path = "images/$id.png"

        try {
            assetManager.open(path).use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.quizImage.setImageBitmap(bitmap)
                binding.quizImage.visibility = View.VISIBLE
            }
        } catch (e: IOException) {
            e.printStackTrace()
            binding.quizImage.setImageResource(R.drawable.quiz)
        }
    }

    private fun finishQuiz() {
        quizFinished = true
        answered = true
        timer?.cancel()

        binding.tvQuestion.text = "Result: $correctCount / ${questions.size}"
        binding.tvTimer.text = "00:00"

        // открываем экран результатов
        (activity as MainActivity).result(
            correct = correctCount,
            wrong = wrongCount,
            skipped = skippedCount,
            total = questions.size,
            states = answerStates   // MutableList<AnswerState>
        )

        with(binding) {
            btnQuizAnswer1.isEnabled = false
            btnQuizAnswer2.isEnabled = false
            btnQuizAnswer3.isEnabled = false
            btnQuizAnswer4.isEnabled = false

            btnStartQuiz.isEnabled = false
            btnStartQuiz.alpha = 0.5f

            btnSkipOrTryAgain.text = getString(R.string.try_again)
            btnSkipOrTryAgain.isEnabled = true
            btnSkipOrTryAgain.alpha = 1f
        }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(questionTimeMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                if (!answered && !quizFinished) {
                    handleTimeUp()
                }
            }
        }.start()
    }

    private fun handleTimeUp() {
        answered = true

        val q = questions[currentIndex]

        with(binding) {
            btnQuizAnswer1.isEnabled = false
            btnQuizAnswer2.isEnabled = false
            btnQuizAnswer3.isEnabled = false
            btnQuizAnswer4.isEnabled = false
        }

        // показываем правильный ответ
        val correctBtn = when (q.correctIndex) {
            0 -> binding.btnQuizAnswer1
            1 -> binding.btnQuizAnswer2
            2 -> binding.btnQuizAnswer3
            else -> binding.btnQuizAnswer4
        }
        correctBtn.setAnswerState(AnswerState.CORRECT)

        // время вышло — считаем как пропущенный
        skippedCount++
        answerStates[currentIndex] = AnswerState.DEFAULT  // серым в результатах

        binding.btnStartQuiz.isEnabled = true
        binding.btnStartQuiz.alpha = 1f
    }

    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun onAnswerClick(index: Int, button: AppCompatButton) {
        if (answered || quizFinished) return
        val q = questions[currentIndex]
        answered = true

        with(binding) {
            btnQuizAnswer1.isEnabled = false
            btnQuizAnswer2.isEnabled = false
            btnQuizAnswer3.isEnabled = false
            btnQuizAnswer4.isEnabled = false
        }

        if (index == q.correctIndex) {
            correctCount++
            answerStates[currentIndex] = AnswerState.CORRECT
            button.setAnswerState(AnswerState.CORRECT)
        } else {
            wrongCount++
            answerStates[currentIndex] = AnswerState.WRONG
            button.setAnswerState(AnswerState.WRONG)

            val correctBtn = when (q.correctIndex) {
                0 -> binding.btnQuizAnswer1
                1 -> binding.btnQuizAnswer2
                2 -> binding.btnQuizAnswer3
                else -> binding.btnQuizAnswer4
            }
            correctBtn.setAnswerState(AnswerState.CORRECT)
        }

        binding.btnStartQuiz.isEnabled = true
        binding.btnStartQuiz.alpha = 1f
    }

    override fun onDestroyView() {
        timer?.cancel()
        super.onDestroyView()
    }
}

// экстеншен для состояния кнопки
private fun AppCompatButton.setAnswerState(state: AnswerState) {
    when (state) {
        AnswerState.DEFAULT -> {
            setBackgroundResource(R.drawable.bg_quiz_answer_default)
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0, R.drawable.ic_check_default, 0
            )
        }
        AnswerState.CORRECT -> {
            setBackgroundResource(R.drawable.bg_quiz_answer_correct)
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0, R.drawable.ic_checked, 0
            )
        }
        AnswerState.WRONG -> {
            setBackgroundResource(R.drawable.bg_quiz_answer_wrong)
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0, R.drawable.ic_check_default, 0
            )
        }
    }
}
