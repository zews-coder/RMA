package rma.catquiz.cats.api.interfaces

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import rma.catquiz.cats.api.dto.ResultDto

interface IResultsInterface {
    @GET("leaderboard")
    suspend fun getAllResultsForCategory(@Query("category") category: Int): List<ResultDto>

    @POST("leaderboard")
    suspend fun postResult(@Body obj:ResultDto)
}