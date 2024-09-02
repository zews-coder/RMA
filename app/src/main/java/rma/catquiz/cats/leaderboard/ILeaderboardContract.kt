package rma.catquiz.cats.leaderboard

import rma.catquiz.cats.api.dto.ResultDto


interface ILeaderboardContract {
    data class LeaderboardState(
        val isLoading: Boolean = false,
        val error: DetailsError? = null,
        val results: List<ResultDto> = emptyList(),
        val nick: String = ""
    ) {
        sealed class DetailsError {
            data class DataUpdateFailed(val cause: Throwable? = null): DetailsError()
        }
    }
}