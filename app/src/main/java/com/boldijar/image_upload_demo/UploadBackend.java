package com.boldijar.image_upload_demo;

import retrofit.RestAdapter;

/**
 * Created by Paul on 1/26/2016.
 */
public class UploadBackend {
    private static final String ENDPOINT = "http://172.25.12.69/php/upload-image-backend/upload"; // your endpoint
    private RestAdapter mRestAdapter;
    private UploadService mUploadService;

    public UploadBackend() {
        mRestAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).build();
        mUploadService = mRestAdapter.create(UploadService.class);
    }

    public UploadService getUploadService() {
        return mUploadService;
    }
}
