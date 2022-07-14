package com.camu.collection.data.module

import com.camu.collection.data.local.LocalDataSource
import com.camu.collection.data.repository.DutchRepositoryImpl
import com.camu.collection.domain.repository.DutchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideRepository(localDataSource: LocalDataSource): DutchRepository {
        return DutchRepositoryImpl(localDataSource)
    }
}