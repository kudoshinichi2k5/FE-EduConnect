package com.example.doan.api;

import com.example.doan.model.Article;
import com.example.doan.model.BookmarkCheckResponse;
import com.example.doan.model.BookmarkRequest;
import com.example.doan.model.BookmarkListResponse;
import com.example.doan.model.ChatbotRequest;
import com.example.doan.model.ChatbotResponse;
import com.example.doan.model.LoginResponse;
import com.example.doan.model.Mentor;
import com.example.doan.model.RegisterRequest;
import com.example.doan.model.Opportunity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
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

    // ===== OPPORTUNITY =====
    @GET("opportunity")
    Call<List<Opportunity>> getAllOpportunities();

    @GET("opportunity/type/{type}")
    Call<List<Opportunity>> getOpportunitiesByType(
            @Path("type") String type
    );

    @GET("opportunity/{id}")
    Call<Opportunity> getOpportunityById(@Path("id") String id);

    // ===== CHATBOT =====
    @POST("chatbot/ask")
    Call<ChatbotResponse> askChatbot(@Body ChatbotRequest request);

    // ===== ARTICLE =====
    @GET("article")
    Call<List<Article>> getAllArticles();

    @GET("article/{id}")
    Call<Article> getArticleById(@Path("id") String id);

    // ===== BOOKMARK =====
    @GET("bookmark/check")
    Call<BookmarkCheckResponse> checkBookmark(
            @Query("MaNguoiDung") String maNguoiDung,
            @Query("TargetId") String targetId,
            @Query("TargetType") String targetType
    );

    @POST("bookmark/add")
    Call<Void> addBookmark(@Body BookmarkRequest request);

    @HTTP(method = "DELETE", path = "bookmark/remove", hasBody = true)
    Call<Void> removeBookmark(@Body BookmarkRequest request);

    @GET("bookmark/user/{uid}")
    Call<BookmarkListResponse> getBookmarksByUser(
            @Path("uid") String uid
    );


    // ===== MENTOR =====
    // Lấy danh sách mentor
    @GET("mentor")
    Call<List<Mentor>> getAllMentors();

    // Lấy mentor theo ID
    @GET("mentor/{id}")
    Call<Mentor> getMentorById(@Path("id") String id);

    // Search mentor
    @GET("mentor/search")
    Call<List<Mentor>> searchMentor(@Query("q") String keyword);
}
