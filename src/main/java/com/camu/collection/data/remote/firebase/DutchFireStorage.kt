package com.camu.collection.data.remote.firebase

import android.net.Uri
import com.camu.collection.data.utils.CMLog
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.tasks.await

class DutchFireStorage {
    private val storageRef = FirebaseStorage.getInstance().reference

    suspend fun uploadImage(storageName: String, uri: String): String? {
        if (uri == "") {
            return null
        }

        var result: String? = null
        val imageRef = storageRef.child(storageName)

        imageRef.putFile(Uri.parse(uri)).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
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

    suspend fun uploadImageListInStorage(
        storageName: String,
        imageList: ArrayList<String>?
    ): ArrayList<String>? {
        if(imageList == null) {
            return null
        }

        val resultList = ArrayList<String>()
        val storageRef = FirebaseStorage.getInstance().reference
        val imageStorageRef = storageRef.child(storageName)

        imageList.forEachIndexed { index, uri ->
            if(uri.contains(FIRESTORE_DOWNLOAD_URL)) {
                resultList.add(uri)
                return@forEachIndexed
            }

            val metadata = storageMetadata {
                setCustomMetadata("index", "" + index)
            }

            val lastIndex: Int = uri.lastIndexOf('/')
            if (lastIndex < 0) {
                return@forEachIndexed
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

    companion object {
        private val TAG = DutchFireStorage::class.java.simpleName
        private val FIRESTORE_DOWNLOAD_URL = "https://firebasestorage.googleapis.com"
    }
}