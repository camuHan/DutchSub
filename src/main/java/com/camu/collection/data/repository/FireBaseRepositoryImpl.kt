package com.camu.collection.data.repository

import android.net.Uri
import com.camu.collection.data.remote.RemoteDataSource
import com.camu.collection.domain.model.DutchInfo
import com.camu.collection.domain.model.UserInfoModel
import com.camu.collection.domain.repository.FireBaseRepository
import javax.inject.Inject

class FireBaseRepositoryImpl @Inject constructor(private val remoteDataSource: RemoteDataSource) : FireBaseRepository {
    override suspend fun addUserIfNotExists(): Boolean {
        return remoteDataSource.addUserIfNotExists()
    }

    override suspend fun updateProfileData(userInfoModel: UserInfoModel): Boolean {
        return remoteDataSource.updateProfileData(userInfoModel)
    }

    override suspend fun changeProfileImage(uri: Uri): String? {
        return remoteDataSource.uploadProfileImage(uri.toString())
    }

    /* dutch other */

    override suspend fun getDutchOtherList() : List<DutchInfo>? {
        return remoteDataSource.getDutchOtherList()/*.map { value ->
            mapperToDutchInfoList(value) }*/
//        return mapperToDutchInfoList(localDataSource.getList())
    }

    override suspend fun setDutchOther(dutchInfo: DutchInfo) {
        remoteDataSource.setDutchOther(dutchInfo)
    }
}