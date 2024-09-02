package rma.catquiz.cats.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import rma.catquiz.cats.entities.CatService
import rma.catquiz.di.DispatcherProvider
import rma.catquiz.navigation.catId
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider,
    private val catsService: CatService
) : ViewModel() {

    private val catId: String = savedStateHandle.catId
    private val _catDetailsState = MutableStateFlow(ICatDetailsContract.CatDetailsState(catId = catId))
    val catDetailsState = _catDetailsState.asStateFlow()

    private fun setCatDetailsState (update: ICatDetailsContract.CatDetailsState.() -> ICatDetailsContract.CatDetailsState) =
        _catDetailsState.getAndUpdate(update)

    init {
        observeCatDetails()
    }

    private fun observeCatDetails() {
        viewModelScope.launch {
            setCatDetailsState { copy(isLoading = true) }
            catsService.getCatByIdFlow(id = catId).collect { catInfoDetail ->
                setCatDetailsState { copy(data = catInfoDetail, isLoading = false) }
            }
        }
    }

}
