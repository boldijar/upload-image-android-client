package com.boldijar.image_upload_demo;


import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

/**
 * Created by Paul on 1/26/2016.
 */
public interface UploadService {

    @POST("/upload")
    @Multipart
    void uploadImage(@Part("image") TypedFile image,
                     Callback<UploadResponse> callback);
}
