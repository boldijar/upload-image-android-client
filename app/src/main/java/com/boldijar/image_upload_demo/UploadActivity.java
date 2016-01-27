package com.boldijar.image_upload_demo;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECT_PHOTO = 69;
    private static final int PERMISSION_REQUEST = 12;

    // the views
    private ImageView mImageView;
    private Button mButton;
    private Button mUploadButton;
    private TextView mUrl;
    private Button mCopyButton;


    // other stuff
    private File mFile;
    private UploadBackend mUploadBackend = new UploadBackend();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // create views, add event listeners
        mImageView = (ImageView) findViewById(R.id.imageview);
        mButton = (Button) findViewById(R.id.button);
        mUploadButton = (Button) findViewById(R.id.upload);
        mUrl = (TextView) findViewById(R.id.url);
        mCopyButton = (Button) findViewById(R.id.upload_copy);
        mImageView.setOnClickListener(this);
        mButton.setOnClickListener(this);
        mUploadButton.setOnClickListener(this);
        mCopyButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.upload) {
            uploadFile();
            return;
        }
        if (v.getId() == R.id.upload_copy) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText(mUrl.getText().toString(), mUrl.getText().toString()));
            Toast.makeText(this, "Url copied to clipboard", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!gotStoragePermission()) {
            askStoragePermission();
            return;
        }
        Intent intent = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PHOTO);
        // start intent to get image
    }

    private void uploadFile() {
        if (mFile == null) {
            Toast.makeText(this, "You didn't selected any image!", Toast.LENGTH_SHORT).show();
            return;
        }
        TypedFile typedFile = new TypedFile("image/*", mFile);
        mUploadBackend.getUploadService().uploadImage(typedFile, new Callback<UploadResponse>() {
            @Override
            public void success(UploadResponse uploadResponse, Response response) {
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                mUrl.setText(UploadBackend.ENDPOINT_ROOT + "images/" + uploadResponse.imagePath);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(getApplicationContext(), retrofitError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && null != data) {
            // get selected image from activity result
            Uri selectedImage = data.getData();
            mImageView.setImageURI(selectedImage);

            String imageUrl = getRealPathFromUri(selectedImage);
            mFile = new File(imageUrl);
        }
    }

    /**
     * @param contentUri the uri of the file
     * @return the absolute path of image
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * @return if we accepted the storage permission or not
     */
    private boolean gotStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * ask for permission
     */
    private void askStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST);
    }
}
