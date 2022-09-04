package com.camu.collection.data.define

object DataDefine {
    object DutchDataBase {
        const val DATABASE_NAME = "DutchDataBase.db"
    }

    object FireStoreCollection {
        const val COLLECTION_NAME_CIRCLES = "circles"
        const val COLLECTION_NAME_USERS = "users"
        const val COLLECTION_NAME_DUTCHS = "dutchs"
        const val COLLECTION_NAME_COMMENTS = "comments"
        /* not used */
        const val COLLECTION_NAME_PROFILE_IMAGES = "profileImages"
    }

    object FireBaseStorage {
        const val FIREBASE_STORAGE_PROFILE_IMAGES = "userProfileImages"
        const val FIREBASE_STORAGE_DUTCH_IMAGES = "dutchImages"
        const val FIREBASE_STORAGE_CIRCLE_IMAGES = "circleImages"
        const val FIREBASE_STORAGE_COMMENT_IMAGE = "commentImage"
    }
}