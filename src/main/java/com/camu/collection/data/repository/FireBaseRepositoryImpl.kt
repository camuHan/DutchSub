package com.camu.collection.data.repository

import android.net.Uri
import com.camu.collection.data.remote.RemoteDataSource
import com.camu.collection.domain.model.CommentInfo
import com.camu.collection.domain.model.DutchInfo
import com.camu.collection.domain.model.UserInfoModel
import com.camu.collection.domain.repository.FireBaseRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
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

    /* dutch other */

    override fun getDutchOtherFlowList() : Flow<List<DutchInfo>> {
        return remoteDataSource.getDutchOtherFlowList()
    }

    override suspend fun getDutchOtherList(docSnapshot: DocumentSnapshot?, limitSize: Long) : QuerySnapshot? {
        return remoteDataSource.getDutchOtherList(docSnapshot, limitSize)
    }

    override fun getDutchOther(dutchId: String): Flow<DutchInfo?> {
        return remoteDataSource.getDutchOther(dutchId)
    }

    override suspend fun setDutchOther(dutchInfo: DutchInfo): Boolean {
        return remoteDataSource.setDutchOther(dutchInfo)
    }

    override suspend fun deleteDutchOther(dutchId: String): Boolean {
        return remoteDataSource.deleteDutchOther(dutchId)
    }

    override fun getDutchCommentList(dutchId: String): Flow<List<CommentInfo>> {
        return remoteDataSource.getDutchCommentList(dutchId)
    }

    override suspend fun setDutchComment(commentInfo: CommentInfo): Boolean {
        return remoteDataSource.setDutchComment(commentInfo)
    }

    override suspend fun deleteDutchComment(commentInfo: CommentInfo): Boolean {
        return remoteDataSource.deleteDutchComment(commentInfo)
    }

    override fun dutchLikeEvent(dutchId: String) {
        return remoteDataSource.dutchLikeEvent(dutchId)
    }

    override fun likeEvent(commentInfo: CommentInfo) {
        return remoteDataSource.likeEvent(commentInfo)
    }
}