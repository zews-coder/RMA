package rma.catquiz.cats.quiz

import rma.catquiz.cats.entities.cat.Cat
import rma.catquiz.user.QuizResult
import rma.catquiz.user.User

interface IQuizContract {

    data class QuizState(
        val isLoading: Boolean = false,
        val userData: User,
        val result: QuizResult? = null,
        val cats: List<Cat> = emptyList(),
        val questions: List<QuizQuestion> = emptyList(),
        val points: Float = 0f,
        val questionIndex: Int = 0,
        val timer: Int = 60*Timer.MINUTES, //5min
        val showQuizExitDialog: Boolean = false,
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }

        fun getTimeAsFormat(): String {
            val min = timer/60
            val sec = if (timer%60 < 10) "0${timer%60}" else timer%60
            return "${min}:${sec}"
        }
    }

    data class QuizQuestion(
        val cats: List<Cat>,
        val images: List<String> = emptyList(),
        val questionText: String,
        val correctAnswer: String,
    )

    sealed class QuizUIEvent {
        data class QuestionAnswered(val catAnswer: Cat) : QuizUIEvent()
        data object Exit : QuizUIEvent()
    }
}