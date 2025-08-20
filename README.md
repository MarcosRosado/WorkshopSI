# Workshop SI - Aplicativo Android Moderno

Bem-vindo ao repositório do projeto desenvolvido durante o Workshop SI! Este é um aplicativo Android que demonstra as práticas modernas de desenvolvimento, utilizando Kotlin, Jetpack Compose e outras bibliotecas essenciais do ecossistema Android.

O objetivo deste projeto é fornecer um exemplo prático e funcional que sirva como base de aprendizado para os participantes do workshop, ilustrando como construir uma aplicação robusta, escalável e com uma interface de usuário moderna.

## Funcionalidades (Exemplo)

*   Listagem de dados consumidos de uma API externa (ex: PokeAPI).
*   Detalhes de um item específico.
*   Interface de usuário reativa construída com Jetpack Compose.
*   Navegação entre telas.
*   Injeção de dependência com Hilt.
*   Carregamento de imagens da web.

## Como Rodar o Projeto

1.  Clone este repositório: `git clone https://github.com/MarcosRosado/WorkshopSI`
2.  Abra o projeto no Android Studio.
3.  Aguarde o Gradle sincronizar as dependências.
4.  Execute o aplicativo em um emulador ou dispositivo físico.

## Tecnologias e Bibliotecas Principais

Este projeto utiliza um conjunto de ferramentas e bibliotecas modernas para o desenvolvimento Android. Abaixo estão as principais, com links para suas documentações e recursos úteis:

### 1. Kotlin

Linguagem de programação oficial para o desenvolvimento Android, conhecida por sua concisão, segurança e interoperabilidade com Java.

*   **Site Oficial:** [kotlinlang.org](https://kotlinlang.org/)
*   **Kotlin para Android:** [developer.android.com/kotlin](https://developer.android.com/kotlin)

### 2. Jetpack Compose

O Jetpack Compose é o kit de ferramentas moderno do Android para a criação de interfaces de usuário nativas. Ele simplifica e acelera o desenvolvimento de UIs no Android com menos código, ferramentas poderosas e APIs intuitivas do Kotlin.

*   **Documentação Principal:** [developer.android.com/jetpack/compose](https://developer.android.com/jetpack/compose)
*   **Componentes da UI (androidx.compose.ui):** Elementos básicos para construir layouts. [developer.android.com/jetpack/compose/layouts/basics](https://developer.android.com/jetpack/compose/layouts/basics)
*   **Gráficos (androidx.compose.ui:ui-graphics):** APIs para desenho e manipulação gráfica. [developer.android.com/jetpack/compose/graphics](https://developer.android.com/jetpack/compose/graphics)
*   **Ferramentas (androidx.compose.ui:ui-tooling):** Suporte para previews e outras ferramentas no Android Studio. [developer.android.com/jetpack/compose/tooling](https://developer.android.com/jetpack/compose/tooling)
*   **Material Design 3 (androidx.compose.material3):** Componentes que implementam as diretrizes do Material Design 3, a evolução do Material Design. [developer.android.com/jetpack/compose/designsystems/material3](https://developer.android.com/jetpack/compose/designsystems/material3)

### 3. Hilt - Injeção de Dependência

O Hilt é uma biblioteca de injeção de dependência para Android que reduz o boilerplate de fazer injeção de dependência manual no seu projeto. Ele é construído sobre o Dagger e simplifica seu uso em aplicações Android.

*   **Documentação:** [developer.android.com/training/dependency-injection/hilt-android](https://developer.android.com/training/dependency-injection/hilt-android)
*   **Hilt e Jetpack Compose (hilt-navigation-compose):** Integração para usar Hilt com ViewModels no Compose. [developer.android.com/jetpack/compose/libraries#hilt](https://developer.android.com/jetpack/compose/libraries#hilt)

### 4. Retrofit - Cliente HTTP

Retrofit é um cliente HTTP type-safe para Android e Java, desenvolvido pela Square. Facilita o consumo de APIs REST, transformando-as em interfaces Java/Kotlin.

*   **Site Oficial:** [square.github.io/retrofit/](https://square.github.io/retrofit/)
*   **Conversor Gson (converter-gson):** Para serializar e desserializar objetos Java/Kotlin de/para JSON.

    *Exemplo de API utilizada no workshop:*
*   **PokeAPI:** Uma API RESTful gratuita e bem documentada para dados de Pokémon. Ótima para exemplos práticos de consumo de API.
    *   **Site:** [pokeapi.co](https://pokeapi.co/)
    *   **Documentação:** [pokeapi.co/docs/v2](https://pokeapi.co/docs/v2)

### 5. Coil - Carregamento de Imagens

Coil (Coroutine Image Loader) é uma biblioteca de carregamento de imagens para Android baseada em Kotlin Coroutines. É rápida, leve e fácil de usar.

*   **Repositório (GitHub):** [github.com/coil-kt/coil](https://github.com/coil-kt/coil)
*   **Coil Compose (coil-compose):** Integração para carregar e exibir imagens de forma simples em Jetpack Compose. [coil-kt.github.io/coil/compose/](https://coil-kt.github.io/coil/compose/)

### 6. Navigation Compose - Navegação

Faz parte do Jetpack Navigation e permite a navegação entre Composables de forma declarativa e integrada com o Jetpack Compose.

*   **Documentação:** [developer.android.com/jetpack/compose/navigation](https://developer.android.com/jetpack/compose/navigation)

