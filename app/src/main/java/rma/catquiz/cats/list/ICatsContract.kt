package com.example.catapult.cats.list

import android.service.autofill.UserData
import rma.catquiz.cats.entities.cat.Cat
import rma.catquiz.user.User


interface ICatsContract {
    data class CatsListState(
        val isLoading: Boolean = false,
        val userData: UserData,
        val darkTheme: Boolean,
        val cats: List<Cat> = emptyList(),
        val catsFiltered: List<Cat> = emptyList(),
        val isSearching: Boolean = false,
        val searchText: String = "",
        val error: DetailsError? = null
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }
    }

    sealed class CatsListUIEvent {
        data class SearchQueryChanged(val query: String) : CatsListUIEvent()
        data class ChangeTheme(val bool: Boolean) : CatsListUIEvent()
        data class Logout(val user: User) : CatsListUIEvent()
    }
}