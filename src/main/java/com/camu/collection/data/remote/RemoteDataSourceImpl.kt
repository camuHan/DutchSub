package com.camu.collection.data.remote

import com.camu.collection.data.define.DataDefine.FireBaseStorage.FIREBASE_STORAGE_COMMENT_IMAGE
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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
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
        return fireStorage.upload(
            FIREBASE_STORAGE_PROFILE_IMAGES + "/" + mAuth.currentUser?.uid
            , uri)
    }

    override suspend fun updateProfileData(userInfoModel: UserInfoModel): Boolean {
//        val url = uploadProfileImage(userInfoModel.photoUrl)
        return fireStorage.updateProfile(userInfoModel)
    }

    override fun getDutchOtherFlowList(): Flow<List<DutchInfo>> {
        val flowData = fireStore.getFlowDataListOrderBy(
            COLLECTION_NAME_DUTCHS,
            "modifiedTime",
            Query.Direction.DESCENDING
        )
        return flowData.transform {
            emit(it.toObjects(DutchInfo::class.java))
        }
    }

    override suspend fun getDutchOtherList(docSnapshot: DocumentSnapshot?, limitSize: Long): QuerySnapshot? {
        val data = if(docSnapshot == null) {
            fireStore.getDataListOrderBy(
                COLLECTION_NAME_DUTCHS,
                "modifiedTime",
                Query.Direction.DESCENDING,
                limitSize
            )
        } else {
            fireStore.getDataMoreListOrderBy(
                COLLECTION_NAME_DUTCHS,
                "modifiedTime",
                Query.Direction.DESCENDING,
                docSnapshot,
                limitSize
            )
        }
        return data
//        return data?.toObjects(DutchInfo::class.java) as ArrayList<DutchInfo>
    }

    override fun getDutchOther(dutchId: String): Flow<DutchInfo?> {
        val flowData = fireStore.getFlowData(
            COLLECTION_NAME_DUTCHS,
            dutchId
        )

        fireStore.searchCountEvent(
            COLLECTION_NAME_DUTCHS,
            dutchId
        )

        return flowData.transform {
            emit(it.toObject(DutchInfo::class.java))
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
        dutchInfo.subDutchInfos.forEach { subDutchInfo ->
            val dutchStorageName = FIREBASE_STORAGE_DUTCH_IMAGES + "/" + dutchInfo.dutchId
            subDutchInfo.receiptList = fireStorage.uploadImageFileList(dutchStorageName, subDutchInfo.receiptList) ?: ArrayList()
        }
    }

    override suspend fun deleteDutchOther(dutchId: String): Boolean {
        var result = false
        val dutchStorageName = FIREBASE_STORAGE_DUTCH_IMAGES + "/" + dutchId
        result = fireStore.deleteData(COLLECTION_NAME_DUTCHS, dutchId)
        if(result) {
            val storageList = fireStorage.getList(dutchStorageName)
            storageList.forEach {
                fireStorage.deleteByRef(it)
            }
        }
        return result
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
        var result = false
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
            val dutchStorageCommentName = FIREBASE_STORAGE_DUTCH_IMAGES +
                    "/" + commentInfo.dutchId +
                    "/" + FIREBASE_STORAGE_COMMENT_IMAGE +
                    "_" + commentInfo.commentId
            commentInfo.image = fireStorage.upload(dutchStorageCommentName, commentInfo.image) ?: ""
        }

        result = fireStore.setSubData(
            COLLECTION_NAME_DUTCHS,
            commentInfo.dutchId,
            COLLECTION_NAME_COMMENTS,
            commentInfo, commentInfo.commentId
        )

        if(result) {
            fireStore.commentCountEvent(
                COLLECTION_NAME_DUTCHS,
                commentInfo.dutchId,
                1
            )
        }

        return result
    }

    override suspend fun deleteDutchComment(commentInfo: CommentInfo): Boolean {
        var result = false

        if(commentInfo.image.isNotEmpty()) {
            val dutchStorageCommentName = FIREBASE_STORAGE_DUTCH_IMAGES +
                    "/" + commentInfo.dutchId +
                    "/" + FIREBASE_STORAGE_COMMENT_IMAGE +
                    "_" + commentInfo.commentId
            fireStorage.delete(dutchStorageCommentName)
        }

        result = fireStore.deleteSubData(
            COLLECTION_NAME_DUTCHS,
            commentInfo.dutchId,
            COLLECTION_NAME_COMMENTS,
            commentInfo.commentId
        )

        if(result) {
            fireStore.commentCountEvent(
                COLLECTION_NAME_DUTCHS,
                commentInfo.dutchId,
                -1
            )
        }

        return result
    }

    override fun dutchLikeEvent(dutchId: String) {
        fireStore.dutchLikeEvent(
            COLLECTION_NAME_DUTCHS,
            dutchId
        )
    }

    override fun likeEvent(commentInfo: CommentInfo) {
        fireStore.commentLikeEvent(
            COLLECTION_NAME_DUTCHS,
            commentInfo.dutchId,
            COLLECTION_NAME_COMMENTS,
            commentInfo.commentId
        )
    }
}