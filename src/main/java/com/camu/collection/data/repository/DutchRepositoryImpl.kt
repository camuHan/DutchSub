package com.camu.collection.data.repository

import com.camu.collection.data.db.HomeLocalDutchDAO
import com.camu.collection.data.mapper.mapperToDutchInfoList
import com.camu.collection.domain.model.DutchInfo
import com.camu.collection.domain.repository.DutchRepository

import javax.inject.Inject

class DutchRepositoryImpl @Inject constructor(
    private val dutchDAO: HomeLocalDutchDAO
) : DutchRepository {
    override suspend fun getDutchList() : List<DutchInfo> {
        return mapperToDutchInfoList(dutchDAO.getAll())
//        val test = ArrayList<DutchInfo>()
//        return test
    }
}