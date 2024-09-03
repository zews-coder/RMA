package rma.catquiz.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import rma.catquiz.cats.entities.cat.Cat
import rma.catquiz.cats.entities.cat.CatDao
import rma.catquiz.cats.entities.image.CatGallery
import rma.catquiz.cats.entities.image.CatImageDao


@Database(
    entities = [
        Cat::class, CatGallery::class
    ],
    version = 1,
    exportSchema = true,
//    autoMigrations = [
//        AutoMigration (from = 1, to = 2)
//    ]
)

abstract class AppDataBase: RoomDatabase() {
    abstract fun catDao(): CatDao
    abstract fun catGalleryDao(): CatImageDao
}