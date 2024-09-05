package rma.catquiz.user.history

import rma.catquiz.user.User

interface IHistoryContract {
    data class HistoryState(
        val userData: User,
        val expandedList: List<Boolean> = listOf(false, false, false)
    )
}