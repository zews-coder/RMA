package rma.catquiz.cats.api.interfaces

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rma.catquiz.cats.entities.cat.Cat
import rma.catquiz.cats.entities.cat.CatImage

interface ICatListInterface {
    @GET("breeds")
    suspend fun getAllCats(): List<Cat>

    @GET("breeds/{id}")
    suspend fun getCat(@Path("id") id: String): Cat

    @GET("images/search?limit=20")
    suspend fun getAllCatsPhotos(@Query("breed_ids") id: String): List<CatImage>
}