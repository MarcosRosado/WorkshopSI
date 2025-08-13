package com.example.workshopsi.core.services

import com.example.workshopsi.core.models.Pokemon
import com.example.workshopsi.core.models.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface que define os endpoints da PokeAPI para interação com o serviço de Pokémon.
 * Utiliza Retrofit para realizar as chamadas de rede.
 */
interface PokeApiService {

    /**
     * Recupera uma lista paginada de Pokémon.
     *
     * @param limit O número máximo de Pokémon a serem retornados na lista. O padrão é 20.
     * @param offset O índice inicial da lista de onde os Pokémon devem ser recuperados. O padrão é 0.
     * @return Um objeto [PokemonListResponse] contendo a lista de Pokémon e informações de paginação.
     */
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse

    /**
     * Recupera os detalhes de um Pokémon específico pelo seu nome.
     *
     * @param name O nome do Pokémon a ser recuperado.
     * @return Um objeto [Pokemon] contendo os detalhes do Pokémon.
     */
    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(@Path("name") name: String): Pokemon
}
