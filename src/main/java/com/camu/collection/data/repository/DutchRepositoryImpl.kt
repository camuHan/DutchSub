package com.camu.collection.data.repository

import com.camu.collection.data.local.LocalDataSource
import com.camu.collection.data.mapper.*
import com.camu.collection.domain.model.DutchInfo
import com.camu.collection.domain.model.UserInfoModel
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

    override suspend fun getUnknownUser(): UserInfoModel? {
        return mapperToUserInfo(localDataSource.getUnknownUser())
    }

    override suspend fun setUnknownUser(userInfo: UserInfoModel) {
        localDataSource.setUnknownUser(mapperToUserEntity(userInfo))
    }

    override suspend fun updateUnknownUser(userInfo: UserInfoModel) {
        localDataSource.updateUnknownUser(mapperToUserEntity(userInfo))
    }
}