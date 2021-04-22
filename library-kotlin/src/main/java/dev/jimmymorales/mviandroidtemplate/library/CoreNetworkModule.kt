package dev.jimmymorales.mviandroidtemplate.library

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreNetworkModule {

    private const val BASE_URL = "https://api.example.com"

    @Singleton
    @Provides
    fun providesMoshi(): Moshi = Moshi.Builder().build()

    @Singleton
    @Provides
    fun providesOkhttpClient(): OkHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        )
    }.build()

    @Singleton
    @Provides
    fun providesRetrofit(
        client: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(
            MoshiConverterFactory.create(moshi)
        )
        .build()

    @Singleton
    @Provides
    fun providesService(
        retrofit: Retrofit
    ): RickAndMortyApi = retrofit.create(RickAndMortyApiImpl::class.java)
}
