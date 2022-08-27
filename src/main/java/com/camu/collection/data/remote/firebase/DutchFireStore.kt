package com.camu.collection.data.remote.firebase

import com.camu.collection.data.utils.CMLog
import com.camu.collection.domain.model.CommentInfo
import com.camu.collection.domain.model.UserInfoModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
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
                } else {
                    CMLog.e(TAG, "there is no uid. need to add data")
                    addUserToFirestore(user, collectionName)
                    result = true
                }
            }.await()

        return result
    }

    private fun addUserToFirestore(firebaseUser: FirebaseUser, collectionName: String) {
        val mUserProfile = UserInfoModel()

        mUserProfile.userId = firebaseUser.uid
        mUserProfile.name = firebaseUser.displayName
        mUserProfile.email = firebaseUser.email
        val photoUrl = if (firebaseUser.photoUrl == null) "" else firebaseUser.photoUrl.toString()
        mUserProfile.photoUrl = photoUrl
        mUserProfile.timestampCreated = FieldValue.serverTimestamp().toString()

        setUserData(collectionName, mUserProfile, firebaseUser.uid)

//        mFireStore.collection(collectionName)
//            .document(firebaseUser.uid)
//            .set(mUserProfile)
//            .addOnSuccessListener {
//                CMLog.d(TAG, "DocumentSnapshot successfully written!")
//            }
//            .addOnFailureListener { e -> CMLog.w(TAG, "Error writing document \n$e") }
    }

    fun setUserData(collectionName: String, data: Any, documentId: String) {
        val doc = mFireStore.collection(collectionName)
            .document(documentId)

        mFireStore.runTransaction { transaction ->
//            val snapshot = transaction.get(doc)
            transaction.set(doc, data)
        }
    }

    suspend fun getDataList(collectionName: String): QuerySnapshot? {
//        var list: List<DocumentSnapshot>? = null
        var snapshot: QuerySnapshot? = null
        mFireStore.collection(collectionName).get()
            .addOnCompleteListener {
                if(!it.isSuccessful) {
                    CMLog.e("HSH", "fail in \n + ${it.exception}")
                } else {
                    CMLog.e("HSH", "success in")
                    snapshot = it.result
//                    list = it.result.documents
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

    fun likeEvent(collectionName: String, documentId: String,
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