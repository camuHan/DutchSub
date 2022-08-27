package com.camu.collection.data.local

import com.camu.collection.data.local.db.HomeLocalDutchDAO
import com.camu.collection.data.model.DutchInfoDbEntity
import com.camu.collection.data.model.UserInfoDbEntity
import com.camu.collection.domain.model.UserInfoModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val dutchDAO: HomeLocalDutchDAO
) : LocalDataSource {
    override fun getList(): Flow<List<DutchInfoDbEntity>> {
        return dutchDAO.getAll()
    }

    override fun getData(dutchId: String): DutchInfoDbEntity {
        return DutchInfoDbEntity()//dutchDAO.getItem(dutchId)
    }

    override suspend fun setData(dutchInfo: DutchInfoDbEntity) {
        dutchDAO.insert(dutchInfo)
    }

    override fun deleteData(dutchId: String) {
//        dutchDAO.delete(dutchId)
    }

    override suspend fun deleteData(dutchInfo: DutchInfoDbEntity) {
        dutchDAO.delete(dutchInfo)
    }

    override fun updateData(dutchInfo: DutchInfoDbEntity) {
//        dutchDAO.update(dutchInfo)
    }

    override suspend fun getUnknownUser(): UserInfoDbEntity {
        return dutchDAO.getUserItem(1)
    }

    override suspend fun setUnknownUser(userInfo: UserInfoDbEntity) {
        dutchDAO.insert(userInfo)
    }

    override suspend fun updateUnknownUser(userInfo: UserInfoDbEntity) {
        dutchDAO.update(userInfo)
    }
}