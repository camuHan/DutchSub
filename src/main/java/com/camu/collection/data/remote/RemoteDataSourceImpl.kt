package com.camu.collection.data.remote

import com.camu.collection.data.define.DataDefine.FireBaseStorage.FIREBASE_STORAGE_CIRCLE_IMAGES
import com.camu.collection.data.define.DataDefine.FireBaseStorage.FIREBASE_STORAGE_COMMENT_IMAGE
import com.camu.collection.data.define.DataDefine.FireBaseStorage.FIREBASE_STORAGE_DUTCH_IMAGES
import com.camu.collection.data.define.DataDefine.FireBaseStorage.FIREBASE_STORAGE_PROFILE_IMAGES
import com.camu.collection.data.define.DataDefine.FireStoreCollection.COLLECTION_NAME_CIRCLES
import com.camu.collection.data.define.DataDefine.FireStoreCollection.COLLECTION_NAME_COMMENTS
import com.camu.collection.data.define.DataDefine.FireStoreCollection.COLLECTION_NAME_DUTCHS
import com.camu.collection.data.define.DataDefine.FireStoreCollection.COLLECTION_NAME_REPORTS
import com.camu.collection.data.define.DataDefine.FireStoreCollection.COLLECTION_NAME_USERS
import com.camu.collection.data.mapper.mapperAddFirebaseUser
import com.camu.collection.data.remote.firebase.DutchFireStorage
import com.camu.collection.data.remote.firebase.DutchFireStore
import com.camu.collection.data.utils.CMLog
import com.camu.collection.domain.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
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

    override suspend fun updateProfile(userInfoModel: UserInfoModel) {
        val userId = mAuth.currentUser?.uid ?: return

        val list = ArrayList<BlockUserInfo>()
        list.addAll(userInfoModel.blockUserList)

        fireStore.setBlockListEvent(
            COLLECTION_NAME_USERS,
            list,
            userId
        )
    }

    override fun getUserInfo(): Flow<UserInfoModel?>? {
        val userId = mAuth.currentUser?.uid ?: return null

        val flowData = fireStore.getFlowData(
            COLLECTION_NAME_USERS,
            userId
        )

        return flowData.transform {
            emit(it.toObject(UserInfoModel::class.java))
        }
    }

    override suspend fun getCurrentUserInfo(): UserInfoModel? {
        val userId = mAuth.currentUser?.uid ?: return null
        val data = fireStore.getData(COLLECTION_NAME_USERS, userId)
        return data?.toObject(UserInfoModel::class.java)
    }

    override suspend fun updateProfileDataInFireStorage(userInfoModel: UserInfoModel): Boolean {
        return fireStorage.updateProfile(userInfoModel)
    }

    override suspend fun report(reportInfo: ReportInfo): Boolean {
        val docId = reportInfo.reportedTime + reportInfo.userId
        return fireStore.setData(COLLECTION_NAME_REPORTS, reportInfo, docId)
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
            fireStore.getDataList(
                COLLECTION_NAME_DUTCHS,
                "modifiedTime",
                Query.Direction.DESCENDING,
                "",
                "",
                null,
                limitSize
            )
        } else {
            fireStore.getDataList(
                COLLECTION_NAME_DUTCHS,
                "modifiedTime",
                Query.Direction.DESCENDING,
                "",
                "",
                docSnapshot,
                limitSize
            )
        }
        return data
    }

    override suspend fun getDutchOtherListCircle(
        docSnapshot: DocumentSnapshot?, limitSize: Long, whereValue: String?
    ): QuerySnapshot? {
        val data = if(docSnapshot == null) {
            fireStore.getDataList(
                COLLECTION_NAME_DUTCHS,
                "modifiedTime",
                Query.Direction.DESCENDING,
                "circleInfo.title",
                whereValue,
                null,
                limitSize
            )
        } else {
            fireStore.getDataList(
                COLLECTION_NAME_DUTCHS,
                "modifiedTime",
                Query.Direction.DESCENDING,
                "circleInfo.title",
                whereValue,
                docSnapshot,
                limitSize
            )
        }
        return data
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

    override suspend fun setDutchOther(dutchInfo: DutchInfo?): Boolean {
        if(dutchInfo == null) {
            return false
        }

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

    override suspend fun setCircle(circleInfo: CircleInfo): Boolean {
        circleInfo.createdTime = System.currentTimeMillis().toString()
        mapperAddFirebaseUser(circleInfo, mAuth.currentUser)
        circleInfo.circleId = circleInfo.createdTime + circleInfo.leaderId

        if(circleInfo.circleImage.isNotEmpty()) {
            val dutchStorageCommentName = FIREBASE_STORAGE_CIRCLE_IMAGES +
                    "/" + circleInfo.circleId
            circleInfo.circleImage = fireStorage.upload(dutchStorageCommentName, circleInfo.circleImage) ?: ""
        }

        return fireStore.setData(COLLECTION_NAME_CIRCLES, circleInfo, circleInfo.circleId)
    }

    override suspend fun deleteCircle(circleId: String): Boolean {
        var result = false
        val dutchStorageName = FIREBASE_STORAGE_CIRCLE_IMAGES + "/" + circleId
        result = fireStore.deleteData(COLLECTION_NAME_CIRCLES, circleId)
        if(result) {
            val dutchStorageCommentName = FIREBASE_STORAGE_DUTCH_IMAGES +
                    "/" + circleId
            fireStorage.delete(dutchStorageCommentName)
        }
        return result
    }

    override suspend fun getCircleList(docSnapshot: DocumentSnapshot?, limitSize: Long): QuerySnapshot? {
        val data = if(docSnapshot == null) {
            fireStore.getDataList(
                COLLECTION_NAME_CIRCLES,
                "title",
                Query.Direction.ASCENDING,
                "",
                "",
                null,
                limitSize
            )
        } else {
            fireStore.getDataList(
                COLLECTION_NAME_CIRCLES,
                "title",
                Query.Direction.ASCENDING,
                "",
                "",
                docSnapshot,
                limitSize
            )
        }
        return data
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

    override fun setDutchPasswordEvent(dutchId: String, password: String) {
        fireStore.setDutchPasswordEvent(
            COLLECTION_NAME_DUTCHS,
            dutchId,
            password
        )
    }

    override fun setDutchCircleEvent(dutchId: String, circleInfo: CircleInfo) {
        fireStore.setDutchCircleEvent(
            COLLECTION_NAME_DUTCHS,
            dutchId,
            circleInfo
        )
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