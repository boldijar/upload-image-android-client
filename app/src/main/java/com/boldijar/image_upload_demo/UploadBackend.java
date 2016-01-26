package com.boldijar.image_upload_demo;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit.RestAdapter;

/**
 * Created by Paul on 1/26/2016.
 */
public class UploadBackend {
    private static final String ENDPOINT = "http://boldijar.esy.es/api.php/upload"; // your endpoint
    private RestAdapter mRestAdapter;
    private UploadService mUploadService;

    public UploadBackend() {
        mRestAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).setLogLevel(RestAdapter.LogLevel.FULL).build();
        mUploadService = mRestAdapter.create(UploadService.class);
    }

    public UploadService getUploadService() {
        return mUploadService;
    }
}
