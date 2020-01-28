package com.example.sender;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {

    private static final String BASE_URL = "http://192.168.43.222:5000/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitClient(Context context) {

        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient
                    .Builder()
                    .readTimeout(6, TimeUnit.MINUTES)
                    .connectTimeout(6, TimeUnit.MINUTES)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
