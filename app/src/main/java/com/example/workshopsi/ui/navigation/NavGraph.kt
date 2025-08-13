package com.example.workshopsi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.workshopsi.ui.views.pokemondetail.PokemonDetailScreen
import com.example.workshopsi.ui.views.pokemonlist.PokemonListScreen

/**
 * Objeto que define as rotas de navegação da aplicação.
 */
object Routes {
    /** Rota para a tela de lista de Pokémon. */
    const val POKEMON_LIST = "pokemon_list"
    /**
     * Rota para a tela de detalhes de um Pokémon.
     * Requer o nome do Pokémon como argumento.
     * Exemplo: "pokemon_detail/{pokemonName}"
     */
    const val POKEMON_DETAIL = "pokemon_detail/{pokemonName}"

    /**
     * Gera a rota completa para a tela de detalhes de um Pokémon específico.
     * @param pokemonName O nome do Pokémon para o qual a rota será gerada.
     * @return A string da rota completa, por exemplo, "pokemon_detail/pikachu".
     */
    fun pokemonDetail(pokemonName: String) = "pokemon_detail/$pokemonName"
}

/**
 * Define o gráfico de navegação principal da aplicação.
 *
 * Configura o [NavHost] com as rotas e seus respectivos composables.
 *
 * @param startDestination A rota inicial para o gráfico de navegação.
 *                         O padrão é [Routes.POKEMON_LIST].
 */
@Composable
fun NavGraph(
    startDestination: String = Routes.POKEMON_LIST
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.POKEMON_LIST) {
            PokemonListScreen(
                // viewModel é hiltViewModel() por padrão em PokemonListScreen
                onPokemonClick = { pokemonName ->
                    navController.navigate(Routes.pokemonDetail(pokemonName))
                }
            )
        }
        composable(
            route = Routes.POKEMON_DETAIL,
            arguments = listOf(navArgument("pokemonName") { type = NavType.StringType })
        ) { backStackEntry ->
            val pokemonName = backStackEntry.arguments?.getString("pokemonName")
            // pokemonName pode ser nulo se o argumento não for passado corretamente
            // ou for opcional e estiver faltando.
            // PokemonDetailScreen já lida internamente com pokemonName sendo nulo.
            PokemonDetailScreen(
                navController = navController,
                pokemonName = pokemonName
            )
        }
    }
}
