package com.example.doan.api;

import com.example.doan.model.Article;
import com.example.doan.model.BookmarkCheckResponse;
import com.example.doan.model.ChatbotRequest;
import com.example.doan.model.ChatbotResponse;
import com.example.doan.model.LoginResponse;
import com.example.doan.model.RegisterRequest;
import com.example.doan.model.Opportunity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @GET("opportunity")
    Call<List<Opportunity>> getAllOpportunities();

    @GET("opportunity/type/{type}")
    Call<List<Opportunity>> getOpportunitiesByType(
            @Path("type") String type
    );

    @GET("opportunity/{id}")
    Call<Opportunity> getOpportunityById(@Path("id") String id);

    @POST("chatbot/ask")
    Call<ChatbotResponse> askChatbot(@Body ChatbotRequest request);

    @GET("article")
    Call<List<Article>> getAllArticles();

    @GET("article/{id}")
    Call<Article> getArticleById(@Path("id") String id);

    // Bookmark
    @GET("bookmark/check")
    Call<BookmarkCheckResponse> checkBookmark(
            @Query("MaNguoiDung") String maNguoiDung,
            @Query("MaTinTuc") String maTinTuc
    );
}
