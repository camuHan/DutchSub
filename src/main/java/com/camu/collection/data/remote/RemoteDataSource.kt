package com.camu.collection.data.remote

import com.camu.collection.domain.model.CommentInfo
import com.camu.collection.domain.model.DutchInfo
import com.camu.collection.domain.model.UserInfoModel
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun addUserIfNotExists(): Boolean
    suspend fun uploadProfileImage(uri: String?): String?
    suspend fun updateProfileData(userInfoModel: UserInfoModel): Boolean

    fun getDutchOtherList(): Flow<List<DutchInfo>>
    suspend fun setDutchOther(dutchInfo: DutchInfo)
    suspend fun deleteDutchOther(dutchId: String): Boolean

    fun getDutchCommentList(dutchId: String): Flow<List<CommentInfo>>
    suspend fun setDutchComment(commentInfo: CommentInfo): Boolean
}