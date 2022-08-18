package com.camu.collection.data.remote

import com.camu.collection.data.define.DataDefine.FireBaseStorage.FIREBASE_STORAGE_PROFILE_IMAGES
import com.camu.collection.data.define.DataDefine.FireStoreCollection.COLLECTION_NAME_DUTCHS
import com.camu.collection.data.define.DataDefine.FireStoreCollection.COLLECTION_NAME_USERS
import com.camu.collection.data.mapper.mapperFireBaseToDutchInfoList
import com.camu.collection.data.remote.firebase.DutchFireStorage
import com.camu.collection.data.remote.firebase.DutchFireStore
import com.camu.collection.domain.model.DutchInfo
import com.camu.collection.domain.model.UserInfoModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val fireStorage: DutchFireStorage,
    private val fireStore: DutchFireStore
) : RemoteDataSource{
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun addUserIfNotExists(): Boolean {
        return fireStore.addUserIfNotExists(COLLECTION_NAME_USERS)
    }

    override suspend fun uploadProfileImage(uri: String?): String? {
        val uri = fireStorage.uploadImage(
            FIREBASE_STORAGE_PROFILE_IMAGES + "/" + mAuth.currentUser?.uid
            , uri)
        return uri
    }

    suspend override fun updateProfileData(userInfoModel: UserInfoModel): Boolean {
//        val url = uploadProfileImage(userInfoModel.photoUrl)
        return fireStorage.updateProfile(userInfoModel)
    }

    override suspend fun getDutchOtherList(): List<DutchInfo>? {
        val result = fireStore.getList(COLLECTION_NAME_DUTCHS)
        return mapperFireBaseToDutchInfoList(result)
    }

    override suspend fun setDutchOther(dutchInfo: DutchInfo) {
        fireStore.setData(COLLECTION_NAME_DUTCHS, dutchInfo)
    }
}