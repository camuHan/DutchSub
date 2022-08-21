package com.camu.collection.data.remote

import com.camu.collection.data.define.DataDefine.FireBaseStorage.FIREBASE_STORAGE_DUTCH_IMAGES
import com.camu.collection.data.define.DataDefine.FireBaseStorage.FIREBASE_STORAGE_PROFILE_IMAGES
import com.camu.collection.data.define.DataDefine.FireStoreCollection.COLLECTION_NAME_COMMENTS
import com.camu.collection.data.define.DataDefine.FireStoreCollection.COLLECTION_NAME_DUTCHS
import com.camu.collection.data.define.DataDefine.FireStoreCollection.COLLECTION_NAME_USERS
import com.camu.collection.data.mapper.mapperAddFirebaseUser
import com.camu.collection.data.remote.firebase.DutchFireStorage
import com.camu.collection.data.remote.firebase.DutchFireStore
import com.camu.collection.domain.model.CommentInfo
import com.camu.collection.domain.model.DutchInfo
import com.camu.collection.domain.model.UserInfoModel
import com.google.firebase.auth.FirebaseAuth
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
        return fireStorage.uploadImage(
            FIREBASE_STORAGE_PROFILE_IMAGES + "/" + mAuth.currentUser?.uid
            , uri)
    }

    suspend override fun updateProfileData(userInfoModel: UserInfoModel): Boolean {
//        val url = uploadProfileImage(userInfoModel.photoUrl)
        return fireStorage.updateProfile(userInfoModel)
    }

    override suspend fun getDutchOtherList(): List<DutchInfo>? {
        val result = fireStore.getDataList(COLLECTION_NAME_DUTCHS)
//        val list = result?.toObjects(DutchInfo::class.java)
        return result?.toObjects(DutchInfo::class.java)
    }

    override suspend fun setDutchOther(dutchInfo: DutchInfo) {
        dutchInfo.createdTime = System.currentTimeMillis().toString()
        dutchInfo.modifiedTime = dutchInfo.createdTime
        mapperAddFirebaseUser(dutchInfo, mAuth.currentUser)
        dutchInfo.dutchId = dutchInfo.userId + dutchInfo.modifiedTime

        convertToDutchFireStoreImage(dutchInfo)

        fireStore.setData(COLLECTION_NAME_DUTCHS, dutchInfo, dutchInfo.dutchId)
    }

    private suspend fun convertToDutchFireStoreImage(dutchInfo: DutchInfo) {
        var imageList = ArrayList<String>()
        dutchInfo.subDutchInfos.forEach { subDutchInfo ->
            imageList.add(subDutchInfo.receipt)
        }
        val dutchStorageName = FIREBASE_STORAGE_DUTCH_IMAGES + "/" + dutchInfo.dutchId
        imageList = fireStorage.uploadImageList(dutchStorageName, imageList) ?: ArrayList()

        imageList.forEachIndexed { index, image ->
            dutchInfo.subDutchInfos[index].receipt = image
        }
    }

    override suspend fun deleteDutchOther(dutchId: String): Boolean {
        return fireStore.deleteData(COLLECTION_NAME_DUTCHS, dutchId)
    }

    override suspend fun getDutchCommentList(dutchId: String): List<CommentInfo>? {
        val result = fireStore.getSubDataList(COLLECTION_NAME_DUTCHS, dutchId, COLLECTION_NAME_COMMENTS)
        return result?.toObjects(CommentInfo::class.java)
    }

    override suspend fun setDutchComment(commentInfo: CommentInfo): Boolean {
        commentInfo.createdTime = System.currentTimeMillis().toString()
        commentInfo.modifiedTime = commentInfo.createdTime
        mapperAddFirebaseUser(commentInfo, mAuth.currentUser)
        commentInfo.commentId = commentInfo.writerId + commentInfo.modifiedTime

        if(commentInfo.image.isNotEmpty()) {
            commentInfo.image = fireStorage.uploadImage(COLLECTION_NAME_COMMENTS, commentInfo.image) ?: ""
        }

        return fireStore.setSubData(
            COLLECTION_NAME_DUTCHS,
            commentInfo.dutchId,
            COLLECTION_NAME_COMMENTS,
            commentInfo, commentInfo.commentId
        )
    }
}