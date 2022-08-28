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
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.*
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

    override fun getDutchOtherList(): Flow<List<DutchInfo>> {
        val flowData = fireStore.getFlowDataListOrderBy(
            COLLECTION_NAME_DUTCHS,
            "modifiedTime",
            Query.Direction.DESCENDING
        )
        return flowData.transform {
            emit(it.toObjects(DutchInfo::class.java))
        }
    }

    override suspend fun setDutchOther(dutchInfo: DutchInfo): Boolean {
        dutchInfo.createdTime = System.currentTimeMillis().toString()
        dutchInfo.modifiedTime = dutchInfo.createdTime
        mapperAddFirebaseUser(dutchInfo, mAuth.currentUser)
        dutchInfo.dutchId = dutchInfo.modifiedTime + dutchInfo.userId

        convertToDutchFireStoreImage(dutchInfo)

        return fireStore.setData(COLLECTION_NAME_DUTCHS, dutchInfo, dutchInfo.dutchId)
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

    override fun getDutchCommentList(dutchId: String): Flow<List<CommentInfo>> {
        val flowData = fireStore.getFlowSubDataListOrderBy(
            COLLECTION_NAME_DUTCHS,
            dutchId,
            COLLECTION_NAME_COMMENTS,
            "modifiedTime",
            Query.Direction.ASCENDING
        )
        return flowData.transform {
            emit(
                it.toObjects(CommentInfo::class.java).toList().sortedWith(
                    compareBy<CommentInfo> {
                        it.rootId
                    }.thenBy {
                        it.modifiedTime
                    }
                )
            )
//            emit(it.toObjects(CommentInfo::class.java))
        }
    }

    override suspend fun setDutchComment(commentInfo: CommentInfo): Boolean {
        commentInfo.createdTime = System.currentTimeMillis().toString()
        commentInfo.modifiedTime = commentInfo.createdTime
        if(mAuth.currentUser != null) {
            mapperAddFirebaseUser(commentInfo, mAuth.currentUser)
        }
        commentInfo.commentId = commentInfo.modifiedTime + commentInfo.writerId
                if(commentInfo.rootId.isEmpty()) {
            commentInfo.rootId = commentInfo.commentId
        }

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

    override suspend fun deleteDutchComment(commentInfo: CommentInfo): Boolean {
        return fireStore.deleteSubData(
            COLLECTION_NAME_DUTCHS,
            commentInfo.dutchId,
            COLLECTION_NAME_COMMENTS,
            commentInfo.commentId
        )
    }

    override fun likeEvent(commentInfo: CommentInfo) {
        fireStore.likeEvent(
            COLLECTION_NAME_DUTCHS,
            commentInfo.dutchId,
            COLLECTION_NAME_COMMENTS,
            commentInfo.commentId
        )
    }
}