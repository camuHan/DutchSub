package com.camu.collection.data.remote.firebase

import com.camu.collection.data.utils.CMLog
import com.camu.collection.domain.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private val TAG = DutchFireStorage::class.java.simpleName

class DutchFireStore {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mFireStore = FirebaseFirestore.getInstance()

    suspend fun addUserIfNotExists(collectionName: String): Boolean {
        var result = false
        val user = mAuth.currentUser ?: return result

        mFireStore.collection(collectionName).document(user.uid).get()
            .addOnCompleteListener {
                if(it.isSuccessful && it.result.exists()) {
                    CMLog.e(TAG, "uid is already exists. : " + user.uid)
                    result = true
                } else {
                    CMLog.e(TAG, "there is no uid. need to add data")
                }
            }.await()
        if(!result) {
            result = addUserToFirestore(user, collectionName)
        }

        return result
    }

    private suspend fun addUserToFirestore(firebaseUser: FirebaseUser, collectionName: String): Boolean {
        val mUserProfile = UserInfoModel()

        mUserProfile.userId = firebaseUser.uid
        mUserProfile.name = firebaseUser.displayName
        mUserProfile.email = firebaseUser.email
        val photoUrl = if (firebaseUser.photoUrl == null) "" else firebaseUser.photoUrl.toString()
        mUserProfile.photoUrl = photoUrl
        mUserProfile.timestampCreated = FieldValue.serverTimestamp().toString()

        return setData(collectionName, mUserProfile, firebaseUser.uid)
    }

    fun setBlockListEvent(collectionName: String, blockList: ArrayList<BlockUserInfo>, documentId: String) {
        val doc = mFireStore.collection(collectionName)
            .document(documentId)

        mFireStore.runTransaction { transaction ->
            val contentDTO = transaction.get(doc).toObject(UserInfoModel::class.java) ?: return@runTransaction
            contentDTO.blockUserList = blockList

            transaction.set(doc, contentDTO)
        }
    }

    fun setUserData(collectionName: String, data: Any, documentId: String) {
        val doc = mFireStore.collection(collectionName)
            .document(documentId)

        mFireStore.runTransaction { transaction ->
//            val snapshot = transaction.get(doc)
            transaction.set(doc, data)
        }
    }

    suspend fun getData(
        collectionName: String,
        documentId: String
    ): DocumentSnapshot? {

        return mFireStore.collection(collectionName)
            .document(documentId).get().await()
    }

    suspend fun getDataList(
        collectionName: String,
        order: String,
        direction: Query.Direction,
        whereField: String,
        whereFieldValue: String?,
        docSnapshot: DocumentSnapshot?,
        limitSize: Long
    ): QuerySnapshot? {
        var snapshot: QuerySnapshot? = null
        val collection = mFireStore.collection(collectionName)
        var query = collection.orderBy(order, direction)

        if(whereFieldValue != null && whereFieldValue.isNotEmpty()) {
            query = query.whereEqualTo(whereField, whereFieldValue)
        }

        if(docSnapshot != null) {
            query = query.startAfter(docSnapshot)
        }
        if(limitSize > 0) {
            query = query.limit(limitSize)
        }

        query.get().addOnCompleteListener {
                if(!it.isSuccessful) {
                    CMLog.e("HSH", "fail in \n + ${it.exception}")
                } else {
                    CMLog.e("HSH", "success in")
                    snapshot = it.result
                }
            }.await()

        return snapshot
    }

    suspend fun getDataMoreList(
        collectionName: String,
        order: String,
        direction: Query.Direction,
        docSnapshot: DocumentSnapshot,
        limitSize: Long
    ): QuerySnapshot? {
        var snapshot: QuerySnapshot? = null
        mFireStore.collection(collectionName)
            .orderBy(order, direction)
            .startAfter(docSnapshot)
            .limit(limitSize).get()
            .addOnCompleteListener {
                if(!it.isSuccessful) {
                    CMLog.e("HSH", "fail in \n + ${it.exception}")
                } else {
                    CMLog.e("HSH", "success in")
                    snapshot = it.result
                }
            }.await()

        return snapshot
    }

    fun getFlowDataList(collectionName: String): Flow<QuerySnapshot> = callbackFlow {
        var snapshot: QuerySnapshot? = null
        val subscription =
            mFireStore.collection(collectionName)
                .addSnapshotListener { snapshot, error ->
            if(snapshot != null) {
                CMLog.e("HSH", "success in")
                trySend(snapshot)
            } else {
                CMLog.e("HSH", "fail in \n + ${error?.message}")
            }
        }
        awaitClose {
            subscription.remove()
        }
    }

    fun getFlowDataListOrderBy(
        collectionName: String,
        order: String,
        direction: Query.Direction
    ): Flow<QuerySnapshot> = callbackFlow {
//        var list: List<DocumentSnapshot>? = null
        var snapshot: QuerySnapshot? = null
        val subscription =
            mFireStore.collection(collectionName)
                .orderBy(order, direction)
                .addSnapshotListener { snapshot, error ->
                    if(snapshot != null) {
                        CMLog.e("HSH", "success in")
                        trySend(snapshot)
                    } else {
                        CMLog.e("HSH", "fail in \n + ${error?.message}")
                    }
                }
        awaitClose {
            subscription.remove()
        }
    }

    fun getFlowData(
        collectionName: String,
        documentId: String
    ): Flow<DocumentSnapshot> = callbackFlow {
        val subscription =
            mFireStore.collection(collectionName)
                .document(documentId)
                .addSnapshotListener { snapshot, error ->
                    if(snapshot != null) {
                        CMLog.e("HSH", "success in")
                        trySend(snapshot)
                    } else {
                        CMLog.e("HSH", "fail in \n + ${error?.message}")
                    }
                }

        awaitClose {
            subscription.remove()
        }
    }

    suspend fun setData(collectionName: String, data: Any, documentId: String): Boolean {
        var result = false
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if(uid != null) {
            mFireStore.collection(collectionName)
                .document(documentId)
                .set(data).addOnCompleteListener {
                    if (it.isSuccessful) {
                        result = true
                    } else {
                        CMLog.e(TAG, "fail in \n + ${it.exception}")
                    }
                }.await()
        }
        return result
    }

    suspend fun deleteData(collectionName: String, documentId: String?): Boolean {
        if(documentId == null) {
            return false
        }

        var result = false
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if(uid != null) {
            mFireStore.collection(collectionName)
                .document(documentId).delete()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        result = true
                    } else {
                        CMLog.e(TAG, "fail in \n + ${it.exception}")
                    }
                }.await()
        }
        return result
    }

    suspend fun getSubDataList(collectionName: String, documentId: String,
                                          subCollectionName: String): QuerySnapshot? {
//        var list: List<DocumentSnapshot>? = null
        var snapshot: QuerySnapshot? = null
        mFireStore.collection(collectionName).document(documentId)
            .collection(subCollectionName).get()
            .addOnCompleteListener {
                if(!it.isSuccessful) {
                    CMLog.e("HSH", "fail in \n + ${it.exception}")
                } else {
                    CMLog.e("HSH", "success in")
                    snapshot = it.result
                }
            }.await()

        return snapshot
    }

    fun getFlowSubDataList(collectionName: String, documentId: String,
                        subCollectionName: String): Flow<QuerySnapshot> = callbackFlow {
        val subscription =
            mFireStore.collection(collectionName).document(documentId)
                .collection(subCollectionName)
                .addSnapshotListener { snapshot, error ->
                    if(snapshot != null) {
                        CMLog.e("HSH", "success in")
                        trySend(snapshot)
                    } else {
                        CMLog.e("HSH", "fail in \n + ${error?.message}")
                    }
                }
        awaitClose {
            subscription.remove()
        }
    }

    fun getFlowSubDataListOrderBy(
        collectionName: String,
        documentId: String,
        subCollectionName: String,
        order: String,
        direction: Query.Direction,
    ): Flow<QuerySnapshot> = callbackFlow {
        val subscription =
            mFireStore.collection(collectionName).document(documentId)
                .collection(subCollectionName)
                .orderBy(order, direction)
                .addSnapshotListener { snapshot, error ->
                    if(snapshot != null) {
                        CMLog.e("HSH", "success in")
                        trySend(snapshot)
                    } else {
                        CMLog.e("HSH", "fail in \n + ${error?.message}")
                    }
                }
        awaitClose {
            subscription.remove()
        }
    }

    suspend fun setSubData(collectionName: String, documentId: String,
                           subCollectionName: String, data: Any, subDocumentId: String): Boolean {
        var result = false
        mFireStore.collection(collectionName)
            .document(documentId)
            .collection(subCollectionName)
            .document(subDocumentId)
            .set(data).addOnCompleteListener {
                if (it.isSuccessful) {
                    result = true
                } else {
                    CMLog.e(TAG, "fail in \n + ${it.exception}")
                }
            }.await()

        return result
    }

    suspend fun deleteSubData(collectionName: String, documentId: String,
                           subCollectionName: String, subDocumentId: String): Boolean {
        var result = false
        mFireStore.collection(collectionName)
            .document(documentId)
            .collection(subCollectionName)
            .document(subDocumentId)
            .delete().addOnCompleteListener {
                if (it.isSuccessful) {
                    result = true
                } else {
                    CMLog.e(TAG, "fail in \n + ${it.exception}")
                }
            }.await()

        return result
    }

    fun searchCountEvent(collectionName: String, documentId: String) {
        val doc = mFireStore.collection(collectionName)
            .document(documentId)
        mFireStore.runTransaction { transaction ->
            val contentDTO = transaction.get(doc).toObject(DutchInfo::class.java) ?: return@runTransaction
            contentDTO.viewCount = contentDTO.viewCount.plus(1)

            transaction.set(doc, contentDTO)
        }
    }

    fun commentCountEvent(collectionName: String, documentId: String, add: Int) {
        val doc = mFireStore.collection(collectionName)
            .document(documentId)
        mFireStore.runTransaction { transaction ->
            val contentDTO = transaction.get(doc).toObject(DutchInfo::class.java) ?: return@runTransaction
            contentDTO.commentCount = contentDTO.commentCount.plus(add)

            transaction.set(doc, contentDTO)
        }
    }

    fun setDutchPasswordEvent(collectionName: String, documentId: String, password: String) {
        val uid = mAuth.currentUser?.uid ?: return
        val doc = mFireStore.collection(collectionName)
            .document(documentId)
        mFireStore.runTransaction { transaction ->
            val contentDTO = transaction.get(doc).toObject(DutchInfo::class.java) ?: return@runTransaction
            contentDTO.password = password

            transaction.set(doc, contentDTO)
        }
    }

    fun setDutchCircleEvent(collectionName: String, documentId: String, circleInfo: CircleInfo) {
        val uid = mAuth.currentUser?.uid ?: return
        val doc = mFireStore.collection(collectionName)
            .document(documentId)
        mFireStore.runTransaction { transaction ->
            val contentDTO = transaction.get(doc).toObject(DutchInfo::class.java) ?: return@runTransaction
            contentDTO.circleInfo = circleInfo

            transaction.set(doc, contentDTO)
        }
    }

    fun dutchLikeEvent(collectionName: String, documentId: String) {
        val uid = mAuth.currentUser?.uid ?: return
        val doc = mFireStore.collection(collectionName)
            .document(documentId)
        mFireStore.runTransaction { transaction ->
            val contentDTO = transaction.get(doc).toObject(DutchInfo::class.java) ?: return@runTransaction
            if(contentDTO.likeList.contains(uid)) {
                contentDTO.likeList.remove(uid)
            } else {
                contentDTO.likeList.add(uid)
            }

            transaction.set(doc, contentDTO)
        }
    }

    fun commentLikeEvent(collectionName: String, documentId: String,
                         subCollectionName: String, subDocumentId: String) {
        val uid = mAuth.currentUser?.uid ?: return
        val doc = mFireStore.collection(collectionName)
            .document(documentId)
            .collection(subCollectionName)
            .document(subDocumentId)
        mFireStore.runTransaction { transaction ->
            val contentDTO = transaction.get(doc).toObject(CommentInfo::class.java)
            if(contentDTO?.likeList?.contains(uid) == true) {
                contentDTO.likeList.remove(uid)
            } else {
                contentDTO?.likeList?.add(uid)
            }
            if (contentDTO != null) {
                transaction.set(doc, contentDTO)
            }
        }
    }
}