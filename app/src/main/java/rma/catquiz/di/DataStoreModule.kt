package rma.catquiz.di

import android.content.Context
import android.service.autofill.UserData
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import rma.catquiz.user.UserDataSerializer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideUserListDataStore (@ApplicationContext context: Context) : DataStore<rma.catquiz.user.UserData> {
        return DataStoreFactory.create(
            produceFile = { context.dataStoreFile(fileName = "users.json") },
            serializer = UserDataSerializer()
        )
    }
}