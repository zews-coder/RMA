package rma.catquiz.user.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import rma.catquiz.user.QuizResult
import rma.catquiz.user.UserDataStore
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _historyState = MutableStateFlow(IHistoryContract.HistoryState(userData = userDataStore.data.value))
    val historyState = _historyState.asStateFlow()

    private val _historyEvent = MutableSharedFlow<IHistoryContract.HistoryUIEvent>()

    fun setHistoryEvent(event: IHistoryContract.HistoryUIEvent) = viewModelScope.launch { _historyEvent.emit(event) }
    private fun setHistoryState(updateWith: IHistoryContract.HistoryState.() -> IHistoryContract.HistoryState) =
        _historyState.getAndUpdate(updateWith)

    init {
        observeEvents()
    }

    fun getBestResult(quiz : String): String {
        val user = historyState.value.userData

        return when (quiz) {
            "leftRightCat" -> user.quiz.bestResult.toString()
            else -> "0.0"
        }
    }

    fun getAllResults(quiz: String): List<QuizResult> {
        val user = historyState.value.userData

        return when (quiz) {
            "leftRightCat" -> user.quiz.resultsHistory.reversed()
            else -> emptyList<QuizResult>()
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            _historyEvent.collect {
                when (it) {
                    is IHistoryContract.HistoryUIEvent.Expanded -> expandedChanged(it.index)
                }
            }
        }
    }

    private fun expandedChanged(index: Int) {
        var expandedList = historyState.value.expandedList.toMutableList()
        expandedList[index] = !expandedList[index]
        if (expandedList[index])
            expandedList = expandedList.mapIndexed { i, bool -> if (i != index) false else bool }.toMutableList()

        viewModelScope.launch {
            setHistoryState { copy(expandedList = expandedList.toImmutableList()) }
        }
    }
}