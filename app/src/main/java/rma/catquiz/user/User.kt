package rma.catquiz.user

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar

@Serializable
data class User(
    val name: String,
    val nickname: String,
    val email: String,
    val darkTheme: Boolean = false,
    val quiz: UserQuiz = UserQuiz.EMPTY,
) {
    companion object {
        val EMPTY = User(
            name = "",
            nickname = "",
            email = "",
            darkTheme = false,
            quiz = UserQuiz.EMPTY
        )
    }
}

@Serializable
data class UserQuiz(
    val resultsHistory: List<QuizResult> = emptyList(),
    val bestResult: Float = 0f,
    val bestPosition: Int = Int.MAX_VALUE,
) {
    companion object {
        val EMPTY = UserQuiz(
            resultsHistory = emptyList(),
            bestResult = 0f,
            bestPosition = Int.MAX_VALUE
        )
    }
}

@Serializable
data class QuizResult(
    val result: Float = 0f,
    val createdAt: Long
) {
    fun getDate(): String {
        return covertToDate(createdAt)
    }

    private fun covertToDate(milliSeconds: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yy hh:mm:ss")
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds)

        return formatter.format(calendar.time)
    }
}