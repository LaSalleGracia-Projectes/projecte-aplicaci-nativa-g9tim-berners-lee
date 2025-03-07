package com.example.critflix.api;

import com.example.critflix.api.ApiService;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://10.0.2.2:8000/api/";
    public static Retrofit getClient(String token) {
        // Configura un cliente OkHttp más robusto
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                // Aumenta los timeouts
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)

                // Interceptor para añadir el token
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

                // Logging interceptor para depuración
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY));

        // Crea el cliente Retrofit
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

    public static ApiService getApiService(String token) {
        return getClient(token).create(ApiService.class);
    }
}