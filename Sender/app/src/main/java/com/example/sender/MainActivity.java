package com.example.sender;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private final int requestCode = 1;
    private Button btnSelectFile;
    private Button btnUploadFile;
    private ArrayList<String> pathsList;
    private ListView lvFilePaths;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        btnSelectFile = findViewById(R.id.button_select_file);
        btnUploadFile = findViewById(R.id.button_upload_file);
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
                new MaterialFilePicker()
                        .withActivity(MainActivity.this)
                        .withRequestCode(requestCode)
                        //.withFilter(Pattern.compile(".*\\.$"))
                        .withFilterDirectories(false)
                        .withHiddenFiles(true)
                        .start();
            }
        });
        btnUploadFile.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                uploadToServer();
            }
        });
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
        if(pathsList.size() < 3){
            Toast.makeText(this, "You must select at least 3 files", Toast.LENGTH_LONG).show();
            return;
        }

        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        UploadAPIs uploadAPIs = retrofit.create(UploadAPIs.class);
        File file1 = new File(pathsList.get(0));
        File file2 = new File(pathsList.get(1));
        File file3 = new File(pathsList.get(2));
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file1);
        MultipartBody.Part part = MultipartBody.Part.createFormData("upload1", file1.getName(), fileReqBody);
        MultipartBody.Part part2 = MultipartBody.Part.createFormData("upload2", file2.getName(), fileReqBody);
        MultipartBody.Part part3 = MultipartBody.Part.createFormData("upload3", file3.getName(), fileReqBody);

        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        Call call = uploadAPIs.uploadImage(part, part2, part3, description);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, retrofit2.Response response) {
                Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failure!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
