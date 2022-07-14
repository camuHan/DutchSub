package com.camu.collection.data.local

import com.camu.collection.data.model.DutchInfoDbEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getList(): Flow<List<DutchInfoDbEntity>>
    fun getData(dutchId: String): DutchInfoDbEntity
    fun setData(dutchInfo: DutchInfoDbEntity)
    fun deleteData(dutchId: String)
    fun deleteData(dutchInfo: DutchInfoDbEntity)
    fun updateData(dutchInfo: DutchInfoDbEntity)
}