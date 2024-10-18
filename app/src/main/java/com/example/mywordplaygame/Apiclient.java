package com.example.mywordplaygame;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Apiclient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://random-word-api.herokuapp.com/";

    public static Retrofit getRetrofitClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
                            .addHeader("X-Api-Key", "YOUR_API_NINJA_KEY")
                            .build()))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}


