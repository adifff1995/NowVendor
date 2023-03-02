package com.app.radarvendor.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://store.bulbul-app.com/api/v1/";
   // https://www.getpostman.com/collections/f078bf5245f03a6e9557

//    public static Retrofit getRetrofitInstance() {
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//
//        OkHttpClient httpClient = new OkHttpClient.Builder()
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Request.Builder ongoing = chain.request().newBuilder();
//                        ongoing.addHeader("Accept", "application/json;versions=1");
//                        if (Hawk.contains("token"))
//                            ongoing.addHeader("Authorization", Hawk.get("token"));
//                        return chain.proceed(ongoing.build());
//                    }
//                }).build();
//
//
//        if (retrofit == null) {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .client(httpClient)
//                    .build();
//        }
//        return retrofit;
//    }

    public static Retrofit getRetrofitInstance() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}
