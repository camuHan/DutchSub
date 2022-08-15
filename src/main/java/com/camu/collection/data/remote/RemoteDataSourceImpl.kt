package com.camu.collection.data.remote

import com.camu.collection.data.define.DataDefine.FireBaseStorage.FIREBASE_STORAGE_PROFILE_IMAGES
import com.camu.collection.data.model.DutchInfoDbEntity
import com.camu.collection.data.remote.firebase.DutchFireStorage
import com.camu.collection.data.remote.firebase.DutchFireStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val fireStorage: DutchFireStorage,
    private val fireStore: DutchFireStore
) : RemoteDataSource{

    override suspend fun uploadProfileImage(uri: String) {
        fireStorage.uploadImage(FIREBASE_STORAGE_PROFILE_IMAGES, uri)
    }

//    suspend fun
}