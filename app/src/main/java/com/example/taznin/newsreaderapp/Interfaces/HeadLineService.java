package com.example.taznin.newsreaderapp.Interfaces;

import com.example.taznin.newsreaderapp.Model.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HeadLineService {
    @GET("top-headlines")
    Call<News> getAllArticales(
            @Query("country") String country,
            @Query("apikey") String apikey
    );
}
