package rma.catquiz.cats.leaderboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import rma.catquiz.cats.entities.CatService
import rma.catquiz.user.UserDataStore
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val catsService: CatService,
    private val usersDataStore: UserDataStore
) : ViewModel() {

    private val _leaderboardState = MutableStateFlow(ILeaderboardContract.LeaderboardState())
    val leaderboardState = _leaderboardState.asStateFlow()

    private fun setLeaderboardState (update: ILeaderboardContract.LeaderboardState.() -> ILeaderboardContract.LeaderboardState) =
        _leaderboardState.getAndUpdate(update)

    init {
        observeCatPhoto()
    }

    private fun observeCatPhoto() {

        viewModelScope.launch {
            setLeaderboardState { copy(isLoading = true) }
            try {
                val list = catsService.fetchAllResultsForCategory(category = categ)
                setLeaderboardState { copy(results = list, nick = usersDataStore.data.value.users[usersDataStore.data.value.pick].nickname) }
            }catch (error: IOException){
                setLeaderboardState { copy(error = ILeaderboardContract.LeaderboardState.DetailsError.DataUpdateFailed(cause = error)) }
            }finally {
                setLeaderboardState { copy(isLoading = false) }
            }

        }
    }

}