package com.camu.collection.data.remote.firebase

import com.camu.collection.data.utils.CMLog
import com.camu.collection.domain.model.UserInfoModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
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
        mUserProfile.timestapCreated = FieldValue.serverTimestamp().toString()

        mFireStore.collection(collectionName)
            .document(firebaseUser.uid)
            .set(mUserProfile)
            .addOnSuccessListener {
                CMLog.d(TAG, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e -> CMLog.w(TAG, "Error writing document \n$e") }
    }

    suspend fun getDataList(collectionName: String): QuerySnapshot? {
//        var list: List<DocumentSnapshot>? = null
        var snapshot: QuerySnapshot? = null
        val db = FirebaseFirestore.getInstance()
        db.collection(collectionName).get()
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
        val db = FirebaseFirestore.getInstance()
        val subscription =
            db.collection(collectionName)
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
        val db = FirebaseFirestore.getInstance()
        val subscription =
            db.collection(collectionName)
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
        val db = FirebaseFirestore.getInstance()
        if(uid != null) {
            db.collection(collectionName)
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
        val db = FirebaseFirestore.getInstance()
        if(uid != null) {
            db.collection(collectionName)
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
        val db = FirebaseFirestore.getInstance()
        db.collection(collectionName).document(documentId)
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
        val db = FirebaseFirestore.getInstance()
        val subscription =
            db.collection(collectionName).document(documentId)
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
        direction: Query.Direction
    ): Flow<QuerySnapshot> = callbackFlow {
        val db = FirebaseFirestore.getInstance()
        val subscription =
            db.collection(collectionName).document(documentId)
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
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        if(uid != null) {
            db.collection(collectionName)
                .document(documentId).collection(subCollectionName).document(subDocumentId)
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
}