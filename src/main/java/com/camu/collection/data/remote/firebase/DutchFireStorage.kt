package com.camu.collection.data.remote.firebase

import android.net.Uri
import com.camu.collection.data.utils.CMLog
import com.camu.collection.domain.model.UserInfoModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import java.io.File

class DutchFireStorage {
    private val storageRef = FirebaseStorage.getInstance().reference

    suspend fun upload(storageName: String, uri: String?): String? {
        if (uri == null || uri == "") {
            return null
        }

        var result: String? = null
        val imageRef = storageRef.child(storageName)

        imageRef.putFile(Uri.fromFile(File(uri))).continueWithTask {
            return@continueWithTask imageRef.downloadUrl
        }.addOnCompleteListener{
            if(it.isSuccessful) {
                result= it.result.toString()
            } else {
                CMLog.e(TAG, "fail in \n + ${it.exception}")
            }
        }.await()
        return result
    }

    suspend fun upload(storageName: String, image: ByteArray): String? {
        var result: String? = null
        val imageRef = storageRef.child(storageName)

        imageRef.putBytes(image).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask imageRef.downloadUrl
        }.addOnCompleteListener{
            if(it.isSuccessful) {
                result= it.result.toString()
            } else {
                CMLog.e(TAG, "fail in \n + ${it.exception}")
            }
        }.await()
        return result
    }

    suspend fun delete(storageName: String): Boolean {
        val imageRef = storageRef.child(storageName)
        var result = false
        imageRef.delete().addOnCompleteListener{
            if(it.isSuccessful) {
                result = true
                CMLog.e(TAG, "success in")
            } else {
                CMLog.e(TAG, "fail in \n + ${it.exception}")
            }
        }.await()
        return result
    }

    suspend fun getList(storageName: String): List<StorageReference> {
        val imageRef = storageRef.child(storageName)
        var result: List<StorageReference> = ArrayList()
        imageRef.listAll().addOnCompleteListener{
            if(it.isSuccessful) {
                result = it.result.items
                CMLog.e(TAG, "success in")
            } else {
                CMLog.e(TAG, "fail in \n + ${it.exception}")
            }
        }.await()
        return result
    }

    suspend fun deleteByRef(imageRef: StorageReference): Boolean {
        var result = false
        imageRef.delete().addOnCompleteListener{
            if(it.isSuccessful) {
                result = true
                CMLog.e(TAG, "success in")
            } else {
                CMLog.e(TAG, "fail in \n + ${it.exception}")
            }
        }.await()
        return result
    }

    suspend fun uploadImageFileList(
        storageName: String,
        imageList: ArrayList<String>?
    ): ArrayList<String>? {
        if(imageList == null) {
            return null
        }

        val resultList = ArrayList<String>()
        val storageRef = FirebaseStorage.getInstance().reference
        val imageStorageRef = storageRef.child(storageName)

        for(uri in imageList) {
            if(uri.contains(FIRESTORE_DOWNLOAD_URL)) {
                resultList.add(uri)
                continue
            }

            val lastIndex: Int = uri.lastIndexOf('/')
            if (lastIndex < 0) {
                continue
            }

            val fileName: String = uri.substring(lastIndex + 1)
            val imageRef = imageStorageRef.child(fileName)

            imageRef.putFile(Uri.fromFile(File(uri))).continueWithTask {
                return@continueWithTask imageRef.downloadUrl
            }.addOnCompleteListener{
                if(it.isSuccessful) {
                    resultList.add(it.result.toString())
                } else {
                    CMLog.e(TAG, "fail in \n + ${it.exception}")
                }
            }.await()
        }
        return resultList
    }

    suspend fun uploadImageList(
        storageName: String,
        imageList: ArrayList<String>?
    ): ArrayList<String>? {
        if(imageList == null) {
            return null
        }

        val resultList = ArrayList<String>()
        val storageRef = FirebaseStorage.getInstance().reference
        val imageStorageRef = storageRef.child(storageName)

        for(uri in imageList) {
            if(uri.contains(FIRESTORE_DOWNLOAD_URL)) {
                resultList.add(uri)
                continue
            }

            val lastIndex: Int = uri.lastIndexOf('/')
            if (lastIndex < 0) {
                continue
            }

            val fileName: String = uri.substring(lastIndex + 1)
            val imageRef = imageStorageRef.child(fileName)

            imageRef.putFile(Uri.parse(uri)).continueWithTask {
                return@continueWithTask imageRef.downloadUrl
            }.addOnCompleteListener{
                if(it.isSuccessful) {
                    resultList.add(it.result.toString())
                } else {
                    CMLog.e(TAG, "fail in \n + ${it.exception}")
                }
            }.await()
        }
        return resultList
    }

    suspend fun updateProfile(userInfo: UserInfoModel): Boolean {
        var result = false
        val user = Firebase.auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = userInfo.name
            photoUri = Uri.parse(userInfo.photoUrl)
        }

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    CMLog.d(TAG, "User profile updated.")
                    result = true
                }
            }?.await()
        return result
    }

    companion object {
        private val TAG = DutchFireStorage::class.java.simpleName
        private val FIRESTORE_DOWNLOAD_URL = "https://firebasestorage.googleapis.com"
    }
}