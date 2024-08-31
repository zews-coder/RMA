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
    val quiz: UserQuiz = UserQuiz.EMPTY
){
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
    val bestResult: Double = 0.0,
    val bestPosition: Int = Int.MAX_VALUE,
) {
    companion object {
        val EMPTY = UserQuiz(
            resultsHistory = emptyList(),
            bestResult = 0.0,
            bestPosition = Int.MAX_VALUE
        )
    }
}

@Serializable
data class QuizResult(
    val result: Double = 0.0,
    val createdAt: Long
) {
    fun covertToDate(milliSeconds: Long): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat("dd/MM/yyy hh:mm:ss")

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds)

        //return the value
        return formatter.format(calendar.time)
    }
}