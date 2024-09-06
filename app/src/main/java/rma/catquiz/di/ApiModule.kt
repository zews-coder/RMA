package rma.catquiz.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import rma.catquiz.cats.api.interfaces.ICatListInterface
import rma.catquiz.cats.api.interfaces.IResultsInterface
import rma.catquiz.cats.api.serialization.JsonAndClass
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor {
                val request = it.request().newBuilder()
                    .addHeader(
                        "x-api-key",
                        "live_OvzDco6sUk2pahKSUAGYdBkcj3VYP4cTo5dlARdu0ZBAt2GVYiybk1OK5db3z80x"
                    )
                    .build()
                it.proceed(request)
            }
            .addInterceptor(
                httpLoggingInterceptor
            )
            .build()

    @Singleton
    @Provides
    @Named("CatApiRetrofit")
    fun provideCatApiRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.thecatapi.com/v1/")
        .client(okHttpClient)
        .addConverterFactory(JsonAndClass.asConverterFactory("application/json".toMediaType()))
        .build()

    @Singleton
    @Provides
    @Named("ResultRetrofit")
    fun provideResultRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(" https://rma.finlab.rs/")
        .addConverterFactory(JsonAndClass.asConverterFactory("application/json".toMediaType()))
        .build()

    @Singleton
    @Provides
    fun provideCatApi(@Named("CatApiRetrofit") retrofit: Retrofit): ICatListInterface = retrofit.create(ICatListInterface::class.java)

    @Singleton
    @Provides
    fun provideResultApi(@Named("ResultRetrofit") retrofit: Retrofit): IResultsInterface = retrofit.create(IResultsInterface::class.java)

}