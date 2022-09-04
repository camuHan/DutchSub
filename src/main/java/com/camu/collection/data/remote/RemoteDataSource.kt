package com.camu.collection.data.remote

import com.camu.collection.domain.model.CommentInfo
import com.camu.collection.domain.model.DutchInfo
import com.camu.collection.domain.model.UserInfoModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun addUserIfNotExists(): Boolean
    suspend fun uploadProfileImage(uri: String?): String?
    suspend fun updateProfileData(userInfoModel: UserInfoModel): Boolean

    fun getDutchOtherFlowList(): Flow<List<DutchInfo>>
    suspend fun getDutchOtherList(docSnapshot: DocumentSnapshot?, limitSize: Int): QuerySnapshot?
    fun getDutchOther(dutchId: String): Flow<DutchInfo?>
    suspend fun setDutchOther(dutchInfo: DutchInfo): Boolean
    suspend fun deleteDutchOther(dutchId: String): Boolean

    fun getDutchCommentList(dutchId: String): Flow<List<CommentInfo>>
    suspend fun setDutchComment(commentInfo: CommentInfo): Boolean
    suspend fun deleteDutchComment(commentInfo: CommentInfo): Boolean

    fun dutchLikeEvent(dutchId: String)
    fun likeEvent(commentInfo: CommentInfo)
}