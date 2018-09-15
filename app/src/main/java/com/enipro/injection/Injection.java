package com.enipro.injection;

import com.enipro.data.remote.EniproRestService;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Injection {

    private static final String BASE_URL = "https://enipro.org";

    private static OkHttpClient okHttpClient;
    private static Retrofit retrofitInstance;
    private static EniproRestService userRestService;

    public static EniproRestService eniproRestService() {
        if (userRestService == null) {
            userRestService = getRetrofitInstance().create(EniproRestService.class);
        }
        return userRestService;
    }

    static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(provideHttpLoggingInterceptor())
                    .build();
        }
        return okHttpClient;
    }

    static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return loggingInterceptor;
    }

    static Retrofit getRetrofitInstance() {
        if (retrofitInstance == null) {
            Retrofit.Builder retrofit = new Retrofit.Builder()
                    .client(Injection.getOkHttpClient())
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
            retrofitInstance = retrofit.build();
        }
        return retrofitInstance;
    }

    /***
     * Provides the interceptor for the authorization header token in the application
     * @return the okhttp interceptor containing Authorization header information.
     */
    static Interceptor provideAuthorizationInterceptor() {

        return null;
    }
}
