package com.example.calorietracker.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApi {
    @GET("quotes/random")
    Call<Quote> getRandomQuote();
}