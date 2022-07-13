package com.camu.collection.data.module

import android.content.Context
import androidx.room.Room
import com.camu.collection.data.db.HomeLocalDutchDAO
import com.camu.collection.data.db.HomeRoomDataBase
import com.camu.collection.data.define.DutchDefine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBaseModule {
    @Module
    @InstallIn(SingletonComponent::class)
    class DiModule {
        @Singleton
        @Provides
        fun provideNoteDatabase(@ApplicationContext context: Context) : HomeRoomDataBase {
            return Room
                .databaseBuilder(
                    context,
                    HomeRoomDataBase::class.java,
                    DutchDefine.DutchDataBase.DATABASE_NAME)
                .build()
        }

        @Singleton
        @Provides
        fun provideNoteDAO(dutchDB: HomeRoomDataBase): HomeLocalDutchDAO {
            return dutchDB.homeDutchDAO()
        }
    }
}