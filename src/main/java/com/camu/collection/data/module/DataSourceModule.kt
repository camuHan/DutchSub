package com.camu.collection.data.module

import com.camu.collection.data.local.LocalDataSource
import com.camu.collection.data.local.LocalDataSourceImpl
import com.camu.collection.data.local.db.HomeLocalDutchDAO
import com.camu.collection.data.remote.RemoteDataSource
import com.camu.collection.data.remote.RemoteDataSourceImpl
import com.camu.collection.data.remote.firebase.DutchFireStorage
import com.camu.collection.data.remote.firebase.DutchFireStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideLocalDataSource(dutchDAO: HomeLocalDutchDAO): LocalDataSource {
        return LocalDataSourceImpl(dutchDAO)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(fireStorage: DutchFireStorage, fireStore: DutchFireStore): RemoteDataSource {
        return RemoteDataSourceImpl(fireStorage, fireStore)
    }
}