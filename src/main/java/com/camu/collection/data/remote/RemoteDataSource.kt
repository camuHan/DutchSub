package com.camu.collection.data.remote

import com.camu.collection.data.model.DutchInfoDbEntity
import com.camu.collection.domain.model.DutchInfo
import com.camu.collection.domain.model.UserInfoModel
import com.google.firebase.auth.UserInfo
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun addUserIfNotExists(): Boolean
    suspend fun uploadProfileImage(uri: String?): String?
    suspend fun updateProfileData(userInfoModel: UserInfoModel): Boolean

    suspend fun getDutchOtherList(): List<DutchInfo>?
    suspend fun setDutchOther(dutchInfo: DutchInfo)
}