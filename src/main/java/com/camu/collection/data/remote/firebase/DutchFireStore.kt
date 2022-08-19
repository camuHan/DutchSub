package com.camu.collection.data.remote.firebase

import com.camu.collection.data.utils.CMLog
import com.camu.collection.domain.model.UserInfoModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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

    suspend fun getList(collectionName: String): List<DocumentSnapshot>? {
        var list: List<DocumentSnapshot>? = null
        val db = FirebaseFirestore.getInstance()
        db.collection(collectionName).get()
            .addOnCompleteListener {
                if(!it.isSuccessful) {
                    CMLog.e("HSH", "fail in \n + ${it.exception}")
                } else {
                    CMLog.e("HSH", "success in")
                    list = it.result.documents
                }
            }.await()

        return list
    }

    suspend fun setData(collectionName: String, data: Any): Boolean {
        var result = false
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        if(uid != null) {
            db.collection(collectionName)
                .document()
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