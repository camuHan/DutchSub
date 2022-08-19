package com.camu.collection.data.module

import com.camu.collection.data.remote.firebase.DutchFireStorage
import com.camu.collection.data.remote.firebase.DutchFireStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FireBaseModule {
    @Provides
    @Singleton
    fun provideDutchFireStorage(): DutchFireStorage {
        return DutchFireStorage()
    }

    @Provides
    @Singleton
    fun provideDutchFireStore(): DutchFireStore {
        return DutchFireStore()
    }
}