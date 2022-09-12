package com.fictivestudios.docsvisor.apiManager.client

import android.content.Context
import com.fictivestudios.ravebae.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {

    class RetrofitInstance {
        companion object {


            val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            }





            private lateinit var apiService: ApiInterface



            fun getApiService(context: Context): ApiInterface {




                    val retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okhttpClient(context))
                        .build()

                    apiService = retrofit.create(ApiInterface::class.java)




                return apiService
            }


            private fun okhttpClient(context: Context): OkHttpClient {
                return OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(context)/*interceptor*/)
                 //   .connectTimeout(60*1000,TimeUnit.MILLISECONDS)

                    .build()
            }



        }
    }

}