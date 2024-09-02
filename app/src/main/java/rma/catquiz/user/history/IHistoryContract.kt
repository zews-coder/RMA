package rma.catquiz.user.history

import rma.catquiz.user.UserData

interface IHistoryContract {

    data class HistoryState(
        val userData: UserData,
        val expandedList: List<Boolean> = listOf(false, false, false)
    )

    sealed class HistoryUIEvent {
        data class Expanded(val index: Int) : HistoryUIEvent()
    }
}