plugins {
    java
    `java-base`
    `java-library`
    `kotlin-dsl`
}

repositories {
    // The org.jetbrains.kotlin.jvm plugin requires a repository
    // where to download the Kotlin compiler dependencies from.
    mavenCentral()
    maven {
        name = "FabricMC"
        url = uri("https://maven.fabricmc.net")
    }
    maven {
        name = "Maven for PR #1724" // https://github.com/neoforged/NeoForge/pull/1724
        url = uri("https://prmaven.neoforged.net/NeoForge/pr1724")
        content {
            includeModule("net.neoforged", "testframework")
            includeModule("net.neoforged", "neoforge")
        }
    }
    maven {
        name = "Mojank"
        url = uri("https://libraries.minecraft.net/")
    }
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
    maven {
        name = "Architectury"
        url = uri("https://maven.architectury.dev/")
    }
}

dependencies {
    implementation("dev.architectury:architectury-loom:1.7-SNAPSHOT")

    implementation("net.fabricmc:fabric-loader:0.16.9")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("dev.architectury:at:1.0.1")

    implementation("commons-codec:commons-codec:1.17.0")
    
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.9.0.202403050737-r")
}