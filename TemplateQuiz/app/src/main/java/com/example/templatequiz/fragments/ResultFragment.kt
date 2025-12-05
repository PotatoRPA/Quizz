package com.example.templatequiz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.templatequiz.R
import com.example.templatequiz.adapters.AnswersAdapter
import com.example.templatequiz.adapters.ResultStatsAdapter
import com.example.templatequiz.additional.AnswerState
import com.example.templatequiz.databinding.FragmentResultBinding
import com.example.templatequiz.models.AnswerItem
import com.example.templatequiz.models.ResultStat

class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding

    // данные результата (можешь передавать из QuizFragment через arguments)
    private var correctCount = 0
    private var wrongCount = 0
    private var skippedCount = 0
    private var totalCount = 0
    private var answerStates: ArrayList<AnswerState> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { bundle ->
            correctCount = bundle.getInt(ARG_CORRECT, 0)
            wrongCount = bundle.getInt(ARG_WRONG, 0)
            skippedCount = bundle.getInt(ARG_SKIPPED, 0)
            totalCount = bundle.getInt(ARG_TOTAL, 0)

            @Suppress("DEPRECATION")
            val list = bundle.getSerializable(ARG_ANSWER_STATES) as? ArrayList<AnswerState>
            if (list != null) answerStates = list
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupScore()
        setupAnswersStrip()
        setupStatsCards()
    }

    private fun setupScore() {
        val percent = if (totalCount > 0) {
            (correctCount * 100) / totalCount
        } else {
            0
        }
        binding.tvScore.text = getString(R.string.score_format, percent)
    }

    private fun setupAnswersStrip() {
        val items: List<AnswerItem> =
            if (answerStates.isNotEmpty()) {
                answerStates.mapIndexed { index, state ->
                    AnswerItem(questionNumber = index + 1, answerState = state)
                }
            } else {
                val count = if (totalCount > 0) totalCount else 10
                (1..count).map { number ->
                    AnswerItem(number, AnswerState.DEFAULT)
                }
            }

        val adapter = AnswersAdapter(items) { item ->
            // сюда придёт клик по номеру вопроса
            // например, можно открыть просмотр конкретного вопроса
            // scrollToQuestion(item.questionNumber - 1)
        }

        binding.answersRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            this.adapter = adapter
        }
    }

    private fun setupStatsCards() {
        val stats = listOf(
            ResultStat(
                iconRes = R.drawable.ic_stat_correct,    // зелёная галочка
                title = "$correctCount questions",
                description = "Correct answer"
            ),
            ResultStat(
                iconRes = R.drawable.ic_stat_wrong,      // красный крестик
                title = "$wrongCount questions",
                description = "Incorrect answer"
            ),
            ResultStat(
                iconRes = R.drawable.ic_stat_skipped,    // иконка «пропуск»
                title = "$skippedCount questions",
                description = "Skipped questions"
            ),
            ResultStat(
                iconRes = R.drawable.ic_stat_total,      // любая иконка для итога
                title = "$totalCount questions",
                description = "Completion questions"
            )
        )

        val statsAdapter = ResultStatsAdapter(stats)

        binding.statsRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = statsAdapter
        }
    }

    companion object {
        private const val ARG_CORRECT = "correctCount"
        private const val ARG_WRONG = "wrongCount"
        private const val ARG_SKIPPED = "skippedCount"
        private const val ARG_TOTAL = "totalCount"
        private const val ARG_ANSWER_STATES = "answerStates"

        fun newInstance(
            correct: Int,
            wrong: Int,
            skipped: Int,
            total: Int,
            answerStates: ArrayList<AnswerState>
        ): ResultFragment {
            return ResultFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CORRECT, correct)
                    putInt(ARG_WRONG, wrong)
                    putInt(ARG_SKIPPED, skipped)
                    putInt(ARG_TOTAL, total)
                    putSerializable(ARG_ANSWER_STATES, answerStates)
                }
            }
        }
    }
}
