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
    private val historyState = _historyState.asStateFlow()

    fun getBestResult(): String {
        val user = historyState.value.userData
        return user.quiz.bestResult.toString()
    }

    fun getAllResults(): List<QuizResult> {
        val user = historyState.value.userData
        return  user.quiz.resultsHistory.reversed()
    }

}