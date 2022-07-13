package com.camu.collection.data.db

import com.camu.collection.data.model.DutchInfoDbEntity

class HomeLocalDB {
    fun insertFavorite(dutchItem: DutchInfoDbEntity) {
        HomeRoomDataBase.getInstance()?.homeDutchDAO()?.insert(dutchItem)
    }

    fun getFavoriteAll(): List<DutchInfoDbEntity>? {
        return HomeRoomDataBase.getInstance()?.homeDutchDAO()?.getAll()
    }

    fun deleteFavorite(dutchId: String) {
        HomeRoomDataBase.getInstance()?.homeDutchDAO()?.delete(dutchId)
    }

//    fun moveFavorite(srcFile: PLFile, destPath: String) {
//        val items = HomeRoomDataBase.getInstance()?.homeFavoriteDAO()?.getItems(srcFile.path) ?: return
//        items.forEach {
//            val item = it
//            var srcPath = srcFile.path
//            if(!srcFile.isDirectory) {
//                srcPath = srcFile.parent!!
//            }
//            item.path = item.path.replace(srcPath, destPath)
//            HomeRoomDataBase.getInstance()?.homeFavoriteDAO()?.update(item)
//        }
//    }

//    fun renameFavorite(file: PLFile, name: String) {
//        if(file.isDirectory) {
//            val targetPath = file.path.replace(file.name, name)
//            moveFavorite(file, targetPath)
//        } else {
//            val item: DustInfoDbEntity =
//                HomeRoomDataBase.getInstance()?.homeFavoriteDAO()?.getItem(file.path) ?: return
//            item.path = item.path.replace(item.fileName, name)
//            item.fileName = name
//            HomeRoomDataBase.getInstance()?.homeFavoriteDAO()?.update(item)
//        }
//    }
}