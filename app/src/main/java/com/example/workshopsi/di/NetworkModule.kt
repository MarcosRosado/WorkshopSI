package com.example.workshopsi.di

import com.example.workshopsi.core.services.PokeApiService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Módulo Hilt para fornecer dependências relacionadas à rede.
 *
 * Este módulo é responsável por configurar e fornecer instâncias de Gson, Retrofit
 * e o [PokeApiService] para serem injetadas em outras partes do aplicativo.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    /**
     * Fornece uma instância singleton de [Gson].
     *
     * @return Uma instância de [Gson].
     */
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    /**
     * Fornece uma instância singleton de [Retrofit].
     *
     * Configura o Retrofit com a URL base da PokeAPI e um conversor Gson.
     *
     * @param gson A instância de [Gson] a ser usada para serialização e desserialização JSON.
     * @return Uma instância configurada de [Retrofit].
     */
    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Fornece uma instância singleton de [PokeApiService].
     *
     * Cria uma implementação da interface [PokeApiService] usando a instância de [Retrofit].
     *
     * @param retrofit A instância de [Retrofit] a ser usada para criar o serviço da API.
     * @return Uma instância de [PokeApiService].
     */
    @Provides
    @Singleton
    fun providePokeApiService(retrofit: Retrofit): PokeApiService {
        return retrofit.create(PokeApiService::class.java)
    }
}
