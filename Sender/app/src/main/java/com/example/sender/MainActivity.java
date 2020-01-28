package com.example.sender;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private final int requestCode = 1001;
    private Button btnSelectFile;
    private Button btnUploadFile;
    private TextView textView2;
    private ArrayList<String> pathsList;
    private ListView lvFilePaths;
    private ArrayAdapter<String> adapter;
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;
    private static final String BASE_URL = "http://192.168.43.222:5000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        btnSelectFile = findViewById(R.id.button_select_file);
        btnUploadFile = findViewById(R.id.button_upload_file);
        textView2 = findViewById(R.id.textView2);
        pathsList = new ArrayList<String>();
        lvFilePaths = findViewById(R.id.list_view_file_paths);
        adapter = new ArrayAdapter<String>(
                this,
                R.layout.list_item,
                pathsList);
        lvFilePaths.setAdapter(adapter);
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        btnSelectFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkPermissionsAndOpenFilePicker();
            }
        });
        btnUploadFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                uploadToServer();
            }
        });
    }

    private void openFilePicker() {
        new MaterialFilePicker()
                .withActivity(MainActivity.this)
                .withRequestCode(requestCode)
                .withHiddenFiles(false)
                .start();
    }

    public void checkPermissions() {
        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET
                    },
                    PackageManager.PERMISSION_GRANTED);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.requestCode && resultCode == RESULT_OK && data != null) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            pathsList.add(filePath);
            adapter.notifyDataSetChanged();
        }
    }

    private void uploadToServer() {
        if (pathsList.size() < 3) {
            Toast.makeText(this, "You must select at least 3 files", Toast.LENGTH_LONG).show();
            return;
        }

        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        UploadAPIs uploadAPIs = retrofit.create(UploadAPIs.class);
        File file1 = new File(pathsList.get(0));
        File file2 = new File(pathsList.get(1));
        File file3 = new File(pathsList.get(2));
//        File file4 = new File(pathsList.get(3));
//        File file5 = new File(pathsList.get(4));

        RequestBody fileReqBody1 = RequestBody.create(MediaType.parse("image/*"), file1);
        RequestBody fileReqBody2 = RequestBody.create(MediaType.parse("image/*"), file2);
        RequestBody fileReqBody3 = RequestBody.create(MediaType.parse("image/*"), file3);
//        RequestBody fileReqBody4 = RequestBody.create(MediaType.parse("image/*"), file4);
//        RequestBody fileReqBody5 = RequestBody.create(MediaType.parse("image/*"), file5);

        MultipartBody.Part part = MultipartBody.Part.createFormData("upload1", file1.getName(), fileReqBody1);
        MultipartBody.Part part2 = MultipartBody.Part.createFormData("upload2", file2.getName(), fileReqBody2);
        MultipartBody.Part part3 = MultipartBody.Part.createFormData("upload3", file3.getName(), fileReqBody3);
//        MultipartBody.Part part4 = MultipartBody.Part.createFormData("upload4", file4.getName(), fileReqBody4);
//        MultipartBody.Part part5 = MultipartBody.Part.createFormData("upload5", file5.getName(), fileReqBody5);

        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        Call<ResponseBody> call = uploadAPIs.uploadImage(part, part2, part3, description);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
//                    InputStream is = response.body().byteStream();
//                    FileOutputStream fos = new FileOutputStream(
//                            new File(Environment.getExternalStorageState(), "ouput.zip")
//                    );
//                    int read = 0;
//                    byte[] buffer = new byte[(int) response.body().contentLength()];
//                    while ((read = is.read(buffer)) > 0) {
//                        fos.write(buffer, 0, read);
//                    }
//
//                    fos.close();
//                    is.close();
                    String body = BASE_URL + "/"+ response.body().string();
                    textView2.setText(Html.fromHtml(body, Html.FROM_HTML_MODE_COMPACT));
                    Toast.makeText(MainActivity.this, "File downloaded", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Failed to download file...", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to download file...", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void checkPermissionsAndOpenFilePicker() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showError();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            openFilePicker();
        }
    }

    private void showError() {
        Toast.makeText(this, "Allow external storage reading", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFilePicker();
                } else {
                    showError();
                }
            }
        }
    }
}
