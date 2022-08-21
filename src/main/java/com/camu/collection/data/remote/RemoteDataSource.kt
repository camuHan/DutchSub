package com.camu.collection.data.remote

import com.camu.collection.domain.model.CommentInfo
import com.camu.collection.domain.model.DutchInfo
import com.camu.collection.domain.model.UserInfoModel

interface RemoteDataSource {
    suspend fun addUserIfNotExists(): Boolean
    suspend fun uploadProfileImage(uri: String?): String?
    suspend fun updateProfileData(userInfoModel: UserInfoModel): Boolean

    suspend fun getDutchOtherList(): List<DutchInfo>?
    suspend fun setDutchOther(dutchInfo: DutchInfo)
    suspend fun deleteDutchOther(dutchId: String): Boolean

    suspend fun getDutchCommentList(dutchId: String): List<CommentInfo>?
    suspend fun setDutchComment(commentInfo: CommentInfo): Boolean
}