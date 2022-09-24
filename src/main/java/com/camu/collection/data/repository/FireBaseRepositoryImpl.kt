package com.camu.collection.data.repository

import android.net.Uri
import com.camu.collection.data.remote.RemoteDataSource
import com.camu.collection.domain.model.*
import com.camu.collection.domain.repository.FireBaseRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FireBaseRepositoryImpl @Inject constructor(private val remoteDataSource: RemoteDataSource) : FireBaseRepository {
    override suspend fun addUserIfNotExists(): Boolean {
        return remoteDataSource.addUserIfNotExists()
    }

    override suspend fun updateProfile(userInfoModel: UserInfoModel) {
        remoteDataSource.updateProfile(userInfoModel)
    }

    override fun getUserInfo(): Flow<UserInfoModel?>? {
        return remoteDataSource.getUserInfo()
    }

    override suspend fun getCurrentUserInfo(): UserInfoModel? {
        return remoteDataSource.getCurrentUserInfo()
    }

    override suspend fun updateProfileDataInFireStorage(userInfoModel: UserInfoModel): Boolean {
        return remoteDataSource.updateProfileDataInFireStorage(userInfoModel)
    }

    override suspend fun changeProfileImage(image: String): String? {
        return remoteDataSource.uploadProfileImage(image)
    }

    override suspend fun report(reportInfo: ReportInfo): Boolean {
        return remoteDataSource.report(reportInfo)
    }

    /* dutch other */

    override fun getDutchOtherFlowList() : Flow<List<DutchInfo>> {
        return remoteDataSource.getDutchOtherFlowList()
    }

    override suspend fun getDutchOtherList(docSnapshot: DocumentSnapshot?, limitSize: Long) : QuerySnapshot? {
        return remoteDataSource.getDutchOtherList(docSnapshot, limitSize)
    }

    override suspend fun getDutchOtherListCircle(docSnapshot: DocumentSnapshot?, limitSize: Long, whereValue: String?): QuerySnapshot? {
        return remoteDataSource.getDutchOtherListCircle(docSnapshot, limitSize, whereValue)
    }

    override fun getDutchOther(dutchId: String): Flow<DutchInfo?> {
        return remoteDataSource.getDutchOther(dutchId)
    }

    override suspend fun setDutchOther(dutchInfo: DutchInfo?): Boolean {
        return remoteDataSource.setDutchOther(dutchInfo)
    }

    override suspend fun deleteDutchOther(dutchId: String): Boolean {
        return remoteDataSource.deleteDutchOther(dutchId)
    }

    override suspend fun setCircle(circleInfo: CircleInfo): Boolean {
        return remoteDataSource.setCircle(circleInfo)
    }

    override suspend fun deleteCircle(circleId: String): Boolean {
        return remoteDataSource.deleteCircle(circleId)
    }

    override suspend fun getCircleList(docSnapshot: DocumentSnapshot?, limitSize: Long): QuerySnapshot? {
        return remoteDataSource.getCircleList(docSnapshot, limitSize)
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

    override fun setDutchPasswordEvent(dutchId: String, password: String) {
        remoteDataSource.setDutchPasswordEvent(dutchId, password)
    }

    override fun setDutchCircleEvent(dutchId: String, circleInfo: CircleInfo) {
        remoteDataSource.setDutchCircleEvent(dutchId, circleInfo)
    }

    override fun dutchLikeEvent(dutchId: String) {
        remoteDataSource.dutchLikeEvent(dutchId)
    }

    override fun likeEvent(commentInfo: CommentInfo) {
        remoteDataSource.likeEvent(commentInfo)
    }
}