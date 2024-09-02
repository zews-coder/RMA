package rma.catquiz.cats.details

import rma.catquiz.cats.entities.cat.Cat


interface ICatDetailsContract {

    data class CatDetailsState(
        val catId: String,
        val isLoading: Boolean = false,
        val data: Cat? = null,
        val error: DetailsError? = null
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }
    }
}