package rma.catquiz.cats.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rma.catquiz.cats.entities.CatService
import rma.catquiz.di.DispatcherProvider
import rma.catquiz.navigation.category
import rma.catquiz.navigation.result
import rma.catquiz.user.UserDataStore
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider,
    private val catsService: CatService,
    private val usersDataStore: UserDataStore
) : ViewModel() {
    private val categ: Int = savedStateHandle.category
    private val ress: Float = savedStateHandle.result
    private val _resultState = MutableStateFlow(IResultContract.ResultState())
    val resultState = _resultState.asStateFlow()
    private val _resultEvents = MutableSharedFlow<IResultContract.ResultUIEvent>()
    fun setEvent(event: IResultContract.ResultUIEvent) = viewModelScope.launch { _resultEvents.emit(event) }

    private fun setResultSate (update: IResultContract.ResultState.() -> IResultContract.ResultState) =
        _resultState.getAndUpdate(update)

    init {
        observeResult()
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            _resultEvents.collect { resultUIEvent ->
                when (resultUIEvent) {
                    is IResultContract.ResultUIEvent.PostResult -> post()
                }
            }
        }
    }

    private fun observeResult() {

        viewModelScope.launch {
            setResultSate { copy(isLoading = true) }
            try {
               setResultSate {
                   copy(
                       category = categ,
                       username =usersDataStore.data.value.nickname,
                       points = ress
                   )
               }
            }catch (error: IOException){
                setResultSate { copy(error = IResultContract.ResultState.DetailsError.DataUpdateFailed(cause = error)) }
            }finally {
                setResultSate { copy( isLoading = false) }
            }

        }
    }
    private fun post(){
        viewModelScope.launch {
            setResultSate { copy(isLoading = true) }
            withContext(dispatcherProvider.io()) {
                val state = resultState.value
                catsService.postResult(state.username,state.points,state.category)
            }
            setResultSate { copy(isPosted = true, isLoading = false) }
        }
    }



}