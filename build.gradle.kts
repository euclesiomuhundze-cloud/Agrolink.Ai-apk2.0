// Ficheiro de configuração global clássico da raiz
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Define as classes do plugin do Android e do Kotlin globalmente
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

