package com.camu.collection.data.mapper

import com.camu.collection.data.model.DutchInfoDbEntity
import com.camu.collection.domain.model.DutchInfo


fun mapperToDutchInfoList(dutchInfoEntityList: List<DutchInfoDbEntity>): List<DutchInfo> {
    return dutchInfoEntityList.toList().map { dutchInfoEntity ->
        mapperToDutchInfo(dutchInfoEntity)
    }
}

fun mapperToDutchInfo(dutchInfoEntity: DutchInfoDbEntity): DutchInfo {
    return DutchInfo(
        userId = dutchInfoEntity.userId,
        dutchId = dutchInfoEntity.dutchId,
        userName = dutchInfoEntity.name,
        title = dutchInfoEntity.title,
        contents = dutchInfoEntity.contents,
        contentsList = dutchInfoEntity.contentsList as ArrayList<String>,
        location = dutchInfoEntity.location,
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
        dutchId = dutchInfo.dutchId,
        name = dutchInfo.userName,
        title = dutchInfo.title,
        contents = dutchInfo.contents,
        contentsList = dutchInfo.contentsList,
        location = dutchInfo.location,
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
