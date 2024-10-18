package com.example.mywordplaygame;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Apiclient {




        private static Retrofit retrofit = null;

        // Replace with the actual base URL of your API
        private static final String BASE_URL = "https://random-word-api.herokuapp.com/word";

        public static Retrofit getRetrofitClient() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())  // Use Gson to convert JSON to objects
                        .build();
            }
            return retrofit;
        }

    }


