package com.camu.collection.data.repository

import com.camu.collection.data.local.LocalDataSource
import com.camu.collection.data.mapper.mapperToDutchEntity
import com.camu.collection.data.mapper.mapperToDutchInfo
import com.camu.collection.data.mapper.mapperToDutchInfoList
import com.camu.collection.domain.model.DutchInfo
import com.camu.collection.domain.repository.DutchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DutchRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
) : DutchRepository {
    override fun getDutchList() : Flow<List<DutchInfo>> {
        return localDataSource.getList().map { value ->
            mapperToDutchInfoList(value) }
//        return mapperToDutchInfoList(localDataSource.getList())
    }

    override suspend fun getDutch(dutchId: String): DutchInfo {
        return mapperToDutchInfo(localDataSource.getData(dutchId))
    }

    override suspend fun setDutch(dutchInfo: DutchInfo) {
        localDataSource.setData(mapperToDutchEntity(dutchInfo))
    }

    override suspend fun deleteDutch(dutchId: String) {
        localDataSource.deleteData(dutchId)
    }

    override suspend fun deleteDutch(dutchInfo: DutchInfo) {
        localDataSource.deleteData(mapperToDutchEntity(dutchInfo))
    }

    override suspend fun updateDutch(dutchInfo: DutchInfo) {
        localDataSource.updateData(mapperToDutchEntity(dutchInfo))
    }
}