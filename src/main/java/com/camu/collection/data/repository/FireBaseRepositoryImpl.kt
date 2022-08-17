package com.camu.collection.data.repository

import android.net.Uri
import com.camu.collection.data.remote.RemoteDataSource
import com.camu.collection.domain.model.UserInfoModel
import com.camu.collection.domain.repository.FireBaseRepository
import com.google.firebase.auth.UserInfo
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
}