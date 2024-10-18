package com.example.mywordplaygame;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WordApiService {
    @GET("word") // This should match the API endpoint
    Call<List<String>> getRandomWord(); // Return type should be List<String>

    @GET("https://api.api-ninjas.com/v1/rhyme") // Ensure this endpoint is correct
    Call<List<String>> getRhymeWord(@Query("word") String word);
}


