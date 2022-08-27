package com.camu.collection.data.local

import com.camu.collection.data.model.DutchInfoDbEntity
import com.camu.collection.data.model.UserInfoDbEntity
import com.camu.collection.domain.model.UserInfoModel
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getList(): Flow<List<DutchInfoDbEntity>>
    fun getData(dutchId: String): DutchInfoDbEntity
    suspend fun setData(dutchInfo: DutchInfoDbEntity)
    fun deleteData(dutchId: String)
    suspend fun deleteData(dutchInfo: DutchInfoDbEntity)
    fun updateData(dutchInfo: DutchInfoDbEntity)

    suspend fun getUnknownUser(): UserInfoDbEntity
    suspend fun setUnknownUser(userInfo: UserInfoDbEntity)
    suspend fun updateUnknownUser(userInfo: UserInfoDbEntity)
}