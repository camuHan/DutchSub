package com.camu.collection.data.mapper

import com.camu.collection.data.model.DutchInfoDbEntity
import com.camu.collection.domain.model.DutchInfo
import com.camu.collection.domain.model.DutchMemberInfo
import com.camu.collection.domain.model.SubDutchInfo


fun mapperToDutchInfoList(dutchInfoEntityList: List<DutchInfoDbEntity>): List<DutchInfo> {
    return dutchInfoEntityList.toList().map { dutchInfoEntity ->
        mapperToDutchInfo(dutchInfoEntity)
    }
}

fun mapperToDutchInfo(dutchInfoEntity: DutchInfoDbEntity): DutchInfo {
    return DutchInfo(
        id = dutchInfoEntity.id.toString(),
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
        likeCount = dutchInfoEntity.likeCount,
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
        likeCount = dutchInfo.likeCount,
        commentCount = dutchInfo.commentCount,
        viewCount = dutchInfo.viewCount,
        password = dutchInfo.password,
        locked = dutchInfo.locked
    )
}
