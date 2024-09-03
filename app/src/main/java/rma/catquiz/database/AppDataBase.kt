package rma.catquiz.database

import androidx.room.Database
import androidx.room.RoomDatabase
import rma.catquiz.cats.entities.cat.Cat
import rma.catquiz.cats.entities.cat.CatDao
import rma.catquiz.cats.entities.image.CatGallery
import rma.catquiz.cats.entities.image.CatGalleryDao

@Database(
    entities = [
        Cat::class, CatGallery::class
    ],
    version = 1,
    exportSchema = true,
)

abstract class AppDataBase: RoomDatabase() {
    abstract fun catDao(): CatDao
    abstract fun catGalleryDao(): CatGalleryDao
}