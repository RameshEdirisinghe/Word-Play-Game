package com.example.mywordplaygame;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WordApiService {

    @GET("random/word") // Adjust the endpoint as necessary
    Call<String> getRandomWord();

    @GET("rhyme/{word}") // Adjust the endpoint for the rhyming words
    Call<List<String>> getRhymeWord(@Path("word") String word);
}


