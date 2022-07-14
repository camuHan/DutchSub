package com.camu.collection.data.module

import android.content.Context
import androidx.room.Room
import com.camu.collection.data.local.db.HomeLocalDutchDAO
import com.camu.collection.data.local.db.HomeRoomDataBase
import com.camu.collection.data.define.DutchDefine
import com.camu.collection.data.local.LocalDataSource
import com.camu.collection.data.repository.DutchRepositoryImpl
import com.camu.collection.domain.repository.DutchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Provides
    fun provideRepository(localDataSource: LocalDataSource): DutchRepository {
        return DutchRepositoryImpl(localDataSource)
    }
}