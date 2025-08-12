package com.example.workshopsi.core.services

import com.example.workshopsi.core.models.Pokemon
import com.example.workshopsi.core.models.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(@Path("name") name: String): Pokemon
}