// Ficheiro de configuração global da raiz compatível com Gradle 9+
plugins {
    // Define a versão do Plugin do Android
    id("com.android.application") version "8.2.2" apply false

    // Define a versão do Kotlin para Android
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false

    // Define a versão do plugin do Jetpack Compose compiler
    id("org.jetbrains.kotlin.plugin.compose") version "1.9.22" apply false

    // Se usares Firebase/Google Services:
    id("com.google.gms.google-services") version "4.4.1" apply false
}

