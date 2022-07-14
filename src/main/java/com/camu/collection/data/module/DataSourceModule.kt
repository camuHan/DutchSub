package com.camu.collection.data.module

import com.camu.collection.data.local.LocalDataSource
import com.camu.collection.data.local.LocalDataSourceImpl
import com.camu.collection.data.local.db.HomeLocalDutchDAO
import com.camu.collection.domain.repository.DutchRepository
import com.camu.collection.domain.usecase.GetDutchListUseCase
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
}