package com.example.doan.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ApiClient
 * - Khởi tạo Retrofit
 * - Kết nối tới Backend tự dựng
 */
public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:5000/api/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
