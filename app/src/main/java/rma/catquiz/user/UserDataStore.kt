package rma.catquiz.user

import androidx.datastore.core.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import okhttp3.internal.toImmutableList
import rma.catquiz.di.DispatcherProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class UserDataStore@Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val dataStore: DataStore<User>
) {
    private val scope = CoroutineScope(dispatcherProvider.io())

    val data = dataStore.data.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = runBlocking { dataStore.data.first() }
    )

    suspend fun addUser(user: User){
        dataStore.updateData { user }
    }

    suspend fun removeUser(user: User){
        dataStore.updateData { User.EMPTY }
    }

    suspend fun addResult(result: QuizResult): User {
        val currentUser = data.value
        val resultsHistory = currentUser.quiz.resultsHistory.toMutableList()
        resultsHistory.add(result)

        val updateResults = currentUser.quiz.copy(
            resultsHistory = resultsHistory.toImmutableList(),
            bestResult = max(currentUser.quiz.bestResult, result.result)
        )

        val updatedUser = currentUser.copy(quiz = updateResults)
        dataStore.updateData {
            updatedUser
        }

        return updatedUser
    }
}