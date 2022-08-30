package com.camu.collection.data.mapper

import com.camu.collection.data.model.DutchInfoDbEntity
import com.camu.collection.data.model.UserInfoDbEntity
import com.camu.collection.domain.model.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot

fun mapperToUserInfo(userInfoDbEntity: UserInfoDbEntity?): UserInfoModel? {
    if(userInfoDbEntity == null) {
        return null
    }
    return UserInfoModel(
        id = userInfoDbEntity.id,
        userId = userInfoDbEntity.userId,
        name = userInfoDbEntity.name,
        email = userInfoDbEntity.email,
        password = userInfoDbEntity.password,
        phoneNumber = userInfoDbEntity.phoneNumber,
        photoUrl = userInfoDbEntity.photoUrl,
        timestampCreated = userInfoDbEntity.timestampCreated,
        isUnknown = true
    )
}

fun mapperToUserEntity(userInfo: UserInfoModel): UserInfoDbEntity {
    return UserInfoDbEntity(
        id = userInfo.id,
        userId = userInfo.userId,
        name = userInfo.name,
        email = userInfo.email,
        password = userInfo.password,
        phoneNumber = userInfo.phoneNumber,
        photoUrl = userInfo.photoUrl,
        timestampCreated = userInfo.timestampCreated,
        isUnknown = true
    )
}

fun mapperToDutchInfoList(dutchInfoEntityList: List<DutchInfoDbEntity>): List<DutchInfo> {
    return dutchInfoEntityList.toList().map { dutchInfoEntity ->
        mapperToDutchInfo(dutchInfoEntity)
    }
}

fun mapperToDutchInfo(dutchInfoEntity: DutchInfoDbEntity): DutchInfo {
    return DutchInfo(
        id = dutchInfoEntity.id,
        userId = dutchInfoEntity.userId,
        dutchId =  dutchInfoEntity.dutchId,
        userName = dutchInfoEntity.userName,
        title = dutchInfoEntity.title,
        dutchMembers = if(dutchInfoEntity.dutchMembers.isNotEmpty()) {
            dutchInfoEntity.dutchMembers as ArrayList<DutchMemberInfo>
        } else {
               ArrayList()
        },
        subDutchInfos = if(dutchInfoEntity.subDutchInfos.isNotEmpty()) {
            dutchInfoEntity.subDutchInfos as ArrayList<SubDutchInfo>
        } else {
            ArrayList()
        },
        contents = dutchInfoEntity.contents,
        contentsList = if(dutchInfoEntity.contentsList.isNotEmpty()) {
            dutchInfoEntity.contentsList as ArrayList<String>
        } else {
            ArrayList()
        },
        location = dutchInfoEntity.location,
        partyTime = dutchInfoEntity.partyTime,
        createdTime = dutchInfoEntity.createdTime,
        modifiedTime = dutchInfoEntity.modifiedTime,
        photoUrl = dutchInfoEntity.photoUrl,
        likeList = if(dutchInfoEntity.likeList.isNotEmpty()) {
            dutchInfoEntity.likeList as ArrayList<String>
        } else {
            ArrayList()
        },
        commentCount = dutchInfoEntity.commentCount,
        viewCount = dutchInfoEntity.viewCount,
        password = dutchInfoEntity.password,
        locked = dutchInfoEntity.locked
    )
}

fun mapperToDutchEntityList(dutchInfoList: List<DutchInfo>): List<DutchInfoDbEntity> {
    return dutchInfoList.toList().map { dutchInfo ->
        mapperToDutchEntity(dutchInfo)
    }
}

fun mapperToDutchEntity(dutchInfo: DutchInfo): DutchInfoDbEntity {
    return DutchInfoDbEntity(
        id = dutchInfo.id,
        userId = dutchInfo.userId,
        dutchId =  dutchInfo.dutchId,
        userName = dutchInfo.userName,
        title = dutchInfo.title,
        dutchMembers = dutchInfo.dutchMembers,
        subDutchInfos = dutchInfo.subDutchInfos,
        contents = dutchInfo.contents,
        contentsList = dutchInfo.contentsList,
        location = dutchInfo.location,
        partyTime = dutchInfo.partyTime,
        createdTime = dutchInfo.createdTime,
        modifiedTime = dutchInfo.modifiedTime,
        photoUrl = dutchInfo.photoUrl,
        likeList = dutchInfo.likeList,
        commentCount = dutchInfo.commentCount,
        viewCount = dutchInfo.viewCount,
        password = dutchInfo.password,
        locked = dutchInfo.locked
    )
}

fun mapperToDutchMemberInfo(subDutchMemberInfo: SubDutchMemberInfo): DutchMemberInfo {
    return DutchMemberInfo()
}

fun mapperToSubDutchMemberInfo(dutchMemberInfo: DutchMemberInfo): SubDutchMemberInfo {
    val subDutchMemberInfo = SubDutchMemberInfo()
    subDutchMemberInfo.photoUrl = dutchMemberInfo.photoUrl
    subDutchMemberInfo.memberName = dutchMemberInfo.memberName
    return subDutchMemberInfo
}

fun mapperToSubDutchMemberInfoList(dutchMemberInfoList: List<DutchMemberInfo>, roundOrder: Int): List<SubDutchMemberInfo> {
    val subDutchMemberList = ArrayList<SubDutchMemberInfo>()
    dutchMemberInfoList.forEach {  dutchMemberInfo ->
        val subDutchMemberInfo = mapperToSubDutchMemberInfo(dutchMemberInfo)
        subDutchMemberInfo.subMemberCount = dutchMemberInfoList.size
        subDutchMemberInfo.roundOrder = roundOrder
        subDutchMemberList.add(subDutchMemberInfo)
    }
    return subDutchMemberList
}

fun mapperAddSubDutchMemberList(
    dutchMembers: ArrayList<DutchMemberInfo>, subDutchMembers: ArrayList<SubDutchMemberInfo>
): ArrayList<DutchMemberInfo> {
    dutchMembers.forEach { mainMember ->
        subDutchMembers.forEach { subMember ->
            if(mainMember.memberName == subMember.memberName) {
                mainMember.subDutchMemberInfo.add(subMember)
            }
        }
    }
    return dutchMembers
}

fun mapperDeleteSubDutchMemberList(
    dutchMembers: ArrayList<DutchMemberInfo>
    , subDutchMembers: ArrayList<SubDutchMemberInfo>
    , position: Int
): ArrayList<DutchMemberInfo> {
    dutchMembers.forEach { mainMember ->
        subDutchMembers.forEach { subMember ->
            if(mainMember.memberName == subMember.memberName) {
                var deletePosition = 0
                mainMember.subDutchMemberInfo.forEachIndexed { index, subDutchMemberInfo ->
                    if(subDutchMemberInfo.roundOrder == position+1) {
                        deletePosition = index
                    }
                }
                mainMember.subDutchMemberInfo.removeAt(deletePosition)
            }
        }
    }

    dutchMembers.forEach { dutchMemberInfo ->
        dutchMemberInfo.subDutchMemberInfo.forEachIndexed { index, subDutchMemberInfo ->
            subDutchMemberInfo.roundOrder = index+1
        }
    }
    return dutchMembers
}

fun mapperToUserInfoModel(firebaseUser: FirebaseUser?): UserInfoModel {
    val userInfoModel = UserInfoModel()
    userInfoModel.userId = firebaseUser?.uid
    userInfoModel.name = firebaseUser?.displayName
    userInfoModel.email = firebaseUser?.email
    userInfoModel.phoneNumber = firebaseUser?.phoneNumber
    userInfoModel.photoUrl = firebaseUser?.photoUrl.toString()
    return userInfoModel
}

/* firestore */
fun mapperFireBaseToDutchInfoList(documentList: List<DocumentSnapshot>?): List<DutchInfo> {
    val dutchInfoList = ArrayList<DutchInfo>()
    documentList?.forEach { documentSnapshot ->
        val item = mapperFireBaseToDutchInfo(documentSnapshot)
        dutchInfoList.add(item)
    }
    return dutchInfoList
}

fun mapperFireBaseToDutchInfo(documentSnapshot: DocumentSnapshot?): DutchInfo {
    val item = DutchInfo()
    if(documentSnapshot == null) {
        return item
    }

    with(documentSnapshot) {
//        item.id = data?.get("id") as Long
        item.userId = data?.get("userId").toString()
        item.dutchId =  data?.get("dutchId").toString()
        item.userName = data?.get("userName").toString()
        item.title = data?.get("title").toString()
        val dutchMemberList = data?.get("dutchMembers")
        if(dutchMemberList is ArrayList<*>) {
            dutchMemberList.forEach {
                item.dutchMembers.add(it as DutchMemberInfo)
            }
        }
        val subDutchInfoList = data?.get("subDutchInfos")
        if(subDutchInfoList is ArrayList<*>) {
            subDutchInfoList.forEach {
                item.subDutchInfos.add(it as SubDutchInfo)
            }
        }
        item.contents = data?.get("contents").toString()
        val contentsList = data?.get("contentsList")
        if(contentsList is ArrayList<*>) {
            contentsList.forEach {
                item.contentsList.add(it.toString())
            }
        }
        item.location = data?.get("location").toString()
        item.partyTime = data?.get("partyTime").toString()
        item.createdTime = data?.get("createdTime").toString()
        item.modifiedTime = data?.get("modifiedTime").toString()
        item.photoUrl = data?.get("photoUrl").toString()
        val likeList = data?.get("likeList")
        if(likeList is ArrayList<*>) {
            likeList.forEach {
                item.likeList.add(it.toString())
            }
        }
        item.commentCount = data?.get("commentCount") as Int
        item.viewCount = data?.get("viewCount") as Int
        item.password = data?.get("password").toString()
        item.locked = data?.get("locked") as Boolean
    }
    return item
}

fun mapperAddFirebaseUser(dutchInfo: DutchInfo, firebaseUser: FirebaseUser?): DutchInfo {
    if(firebaseUser != null) {
        dutchInfo.userId = firebaseUser.uid
        dutchInfo.userName = firebaseUser.displayName ?: ""
        dutchInfo.photoUrl = firebaseUser.photoUrl.toString()
    }
    return dutchInfo
}

fun mapperAddFirebaseUser(commentInfo: CommentInfo, firebaseUser: FirebaseUser?): CommentInfo {
    if(firebaseUser != null) {
        commentInfo.writerId = firebaseUser.uid
        commentInfo.writerName = firebaseUser.displayName ?: ""
        commentInfo.photoUrl = firebaseUser.photoUrl.toString()
    }
    return commentInfo
}