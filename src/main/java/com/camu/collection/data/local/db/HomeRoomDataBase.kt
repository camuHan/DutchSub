package com.camu.collection.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.camu.collection.data.model.DutchInfoDbEntity
import com.camu.collection.data.model.SubDutchInfoDbEntity
import com.camu.collection.data.model.UserInfoDbEntity
import com.camu.collection.data.utils.HomeTypeConverter

@Database(entities = [DutchInfoDbEntity::class, UserInfoDbEntity::class], version = 1, exportSchema = false)
@TypeConverters(HomeTypeConverter::class)
abstract class HomeRoomDataBase : RoomDatabase() {
    abstract fun homeDutchDAO(): HomeLocalDutchDAO

//    companion object {
//        var INSTANCE: HomeRoomDataBase? = null
//
//        fun getInstance(): HomeRoomDataBase? {
//            if (INSTANCE != null) {
//                return INSTANCE
//            }
//
//            return null
//        }
//
//        fun getInstance(context: Context): HomeRoomDataBase? {
//            if (INSTANCE == null) {
//                synchronized(HomeRoomDataBase::class.java) {
//                    INSTANCE = Room.databaseBuilder(context, HomeRoomDataBase::class.java, "DutchDataBase.db").fallbackToDestructiveMigration().build()
//                }
//            }
//            return INSTANCE
//        }
//
//        fun clearDB() {
//            INSTANCE = null
//        }
//    }
}