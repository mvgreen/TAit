package com.cheater;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PhotoActivity extends AppCompatActivity {

    public static final String TESS_DATA = "/tessdata";
    private static final String TAG = PhotoActivity.class.getSimpleName();
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Tess";
    private EditText text;
    private TessBaseAPI tessBaseAPI;
    private Uri outputFileDir;
    private String mCurrentPhotoPath;
    private ArrayList<CharSequence> arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        text = findViewById(R.id.tv1);
        final Activity activity = this;
        this.findViewById(R.id.photo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        arr = new ArrayList<>();
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                if (text.getSelectionEnd() != text.getSelectionStart())
                    return;
                String s = text.getText().subSequence(0, text.getSelectionEnd()).toString();
                arr.add(s);
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

            case R.id.delete_btn:
                if (text.getSelectionEnd() == text.getSelectionStart() && text.getSelectionEnd() != 0)
                    text.getText().delete(0, text.getSelectionEnd());
                break;

            case R.id.confirm_btn:
                Intent data = new Intent();
                data.putCharSequenceArrayListExtra("queries", arr);
                setResult(RESULT_OK, data);
                finish();
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1024);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1024) {
            if (resultCode == Activity.RESULT_OK) {
                startOCR(outputFileDir);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Result canceled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Activity result failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startOCR(Uri imageUri){
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 6;
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
            String result = getText(bitmap);

            text.setText(result);
        }catch (Exception e){
            Log.e(TAG, e.getMessage() == null ? e.getClass().getName() : e.getMessage());
        }
    }

    private String getText(Bitmap bitmap){
        try{
            tessBaseAPI = new TessBaseAPI();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }

        String dataPath = App.getInstance().tessDir.getPath() + "/";
        tessBaseAPI.init(dataPath, "rus");
        tessBaseAPI.setImage(bitmap);
        String retStr = "No result";
        try{
            retStr = tessBaseAPI.getUTF8Text();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.end();
        return retStr;
    }
}
