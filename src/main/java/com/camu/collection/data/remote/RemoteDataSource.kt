package com.camu.collection.data.remote

import com.camu.collection.domain.model.UserInfoModel
import com.google.firebase.auth.UserInfo

interface RemoteDataSource {
    suspend fun addUserIfNotExists(): Boolean
    suspend fun uploadProfileImage(uri: String?): String?
    suspend fun updateProfileData(userInfoModel: UserInfoModel): Boolean
}