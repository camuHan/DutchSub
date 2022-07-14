package com.camu.collection.data.module

import android.content.Context
import androidx.room.Room
import com.camu.collection.data.define.DataDefine
import com.camu.collection.data.local.db.HomeLocalDutchDAO
import com.camu.collection.data.local.db.HomeRoomDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {
    @Singleton
    @Provides
    fun provideDutchDatabase(@ApplicationContext context: Context) : HomeRoomDataBase {
        return Room
            .databaseBuilder(
                context,
                HomeRoomDataBase::class.java,
                DataDefine.DutchDataBase.DATABASE_NAME)
            .build()
    }

    @Singleton
    @Provides
    fun provideDutchDAO(dutchDB: HomeRoomDataBase): HomeLocalDutchDAO {
        return dutchDB.homeDutchDAO()
    }
}