package com.axionesl.medcheck.repository

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

class StorageWriter {
    companion object {
        fun upload(path: String, data: ByteArray, onSuccessListener: OnSuccessListener<UploadTask.TaskSnapshot>?, onFailureListener: OnFailureListener?) {
            Firebase.storage.reference.child(path).putBytes(data)
        }
    }
}