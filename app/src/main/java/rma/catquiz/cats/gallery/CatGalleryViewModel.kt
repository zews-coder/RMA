package rma.catquiz.cats.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rma.catquiz.cats.entities.CatService
import rma.catquiz.di.DispatcherProvider
import rma.catquiz.navigation.catId
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CatGalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider,
    private val catsService: CatService
) : ViewModel() {

    private val catId: String = savedStateHandle.catId
    private val _catGalleryState = MutableStateFlow(ICatGalleryContract.CatGalleryState(catId = catId))
    val catGalleryState = _catGalleryState.asStateFlow()

    private fun setCatGalleryState (update: ICatGalleryContract.CatGalleryState.() -> ICatGalleryContract.CatGalleryState) =
        _catGalleryState.getAndUpdate(update)

    init {
        observeCatGallery()
    }

    private fun observeCatGallery() {
        viewModelScope.launch {
            setCatGalleryState { copy(isLoading = true) }
            try {
                var newPhotos = catsService.getAllCatImagesByIdFlow(id = catId).first()
                if(newPhotos.isEmpty()) {
                    withContext(dispatcherProvider.io()) {
                        catsService.getAllCatsPhotosApi(id = catId)
                    }
                    newPhotos = catsService.getAllCatImagesByIdFlow(id = catId).first()
                }
                setCatGalleryState { copy(photos = newPhotos, isLoading = false) }

            }catch (error: IOException){
                setCatGalleryState { copy(error = ICatGalleryContract.CatGalleryState.DetailsError.DataUpdateFailed(cause = error)) }
            }finally {
                setCatGalleryState { copy(photos = photos, isLoading = false) }
            }
        }
    }

}
