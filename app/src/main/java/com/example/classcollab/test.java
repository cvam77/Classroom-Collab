package com.example.classcollab;

import android.graphics.Bitmap;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class test {
//    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//    byte bb[] = bytes.toByteArray();

    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    StorageReference storageReference = mStorageRef.child("myimages/a.jpg");

}
