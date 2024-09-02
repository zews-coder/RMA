package rma.catquiz.di


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rma.catquiz.cats.entities.cat.CatDao
import rma.catquiz.cats.entities.image.CatImageDao
import rma.catquiz.database.AppDataBase
import rma.catquiz.database.AppDataBaseBuilder
import javax.inject.Singleton

@Module //class in which you can add bindings for types that cannot be constructor injected
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    fun provideCatDao(dataBase: AppDataBase): CatDao = dataBase.catDao()
    @Provides
    fun provideCatGalleryDao(dataBase: AppDataBase): CatImageDao = dataBase.catGalleryDao()

    @Provides
    @Singleton
    fun provideDatabase(appDataBaseBuilder: AppDataBaseBuilder): AppDataBase = appDataBaseBuilder.build()
}