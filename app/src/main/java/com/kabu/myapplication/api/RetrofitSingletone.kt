package com.kabu.myapplication.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitSingletone {
    val prxwURL: String = "prxw.vientimtphcm.vn:23828"
    val normal: String = "authw.vientimtphcm.vn:23818"
    private fun getClientAut(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://$normal")
            .client(provideHttpClient(normal))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getClientPRXW(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://$prxwURL")
            .client(provideHttpClient(prxwURL))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun serviceApi(): ServiceApi = getClientAut().create(ServiceApi::class.java)

    fun serviceApiPRXW(): ServicePRXWApi = getClientPRXW().create(ServicePRXWApi::class.java)

    private fun provideHttpClient(host: String): OkHttpClient {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return OkHttpClient
            .Builder()
            .addInterceptor(imInterceptor(host))
            .addInterceptor(logging)
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    var accessToken: String = "";


    var logging = HttpLoggingInterceptor();

    fun imInterceptor(host: String): Interceptor {
        var authen = "undefined";
        if (accessToken.isNotEmpty()) {
            authen = accessToken;
        }
        return Interceptor { chain ->
            val original = chain.request()
            chain.proceed(
                original.newBuilder()

                    .header("Accept", "application/json, text/plain, */*")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "en-US,en;q=0.9,vi-VN;q=0.8,vi;q=0.7")
                    .header("Connection", "keep-alive")
                    .header("Content-Length", "keep-alive")
                    .header("Content-Type", "application/json")
                    .header("Host", host)
                    .header(
                        "Authorization", authen
                    )
                    .header(
                        "User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
                    )
                    .build()
            )
        }
    }
}