package com.example.doan.api;

import com.example.doan.model.LoginResponse;
import com.example.doan.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * ApiService
 * - Khai báo các API backend
 */
public interface ApiService {

    /**
     * POST /user/login
     * Header: Authorization: Bearer <Firebase_ID_Token>
     */
    @POST("user/login")
    Call<LoginResponse> login(
            @Header("Authorization") String bearerToken
    );

    // ===== REGISTER =====
    @POST("user/register")
    Call<Void> register(
            @Body RegisterRequest request
    );
}
