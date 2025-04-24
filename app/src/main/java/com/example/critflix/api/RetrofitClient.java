package com.example.critflix.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8000/api/";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Boolean.class, new JsonDeserializer<Boolean>() {
                @Override
                public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                    if (json.isJsonPrimitive()) {
                        if (json.getAsJsonPrimitive().isBoolean()) {
                            return json.getAsBoolean();
                        } else if (json.getAsJsonPrimitive().isNumber()) {
                            return json.getAsInt() != 0;
                        } else if (json.getAsJsonPrimitive().isString()) {
                            String stringValue = json.getAsString().toLowerCase();
                            return stringValue.equals("true") || stringValue.equals("1");
                        }
                    }
                    return false;
                }
            })
            .create();

    public static Retrofit getClient(String token) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)

                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", "Bearer " + (token != null ? token : ""))
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                })

                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY));

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
    }

    public static ApiService getApiService(String token) {
        return getClient(token).create(ApiService.class);
    }
}