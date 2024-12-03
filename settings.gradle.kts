pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "Architectury"
            url = uri("https://maven.architectury.dev/")
        }
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net")
        }
        maven {
            name = "NeoForged"
            url = uri("https://maven.neoforged.net/releases")
        }
    }
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
    }
}

rootProject.name = "forgified-fabric-api"

gradle.beforeProject {
    val localPropertiesFile = rootDir.resolve("ffapi.gradle.properties")
    if (localPropertiesFile.exists()) {
        val localProperties = java.util.Properties()
        localProperties.load(localPropertiesFile.inputStream())
        localProperties.forEach { (k, v) -> if (k is String) project.extra.set(k, v) }
    }
}

include("fabric-api-bom")
include("fabric-api-catalog")

include("fabric-api-base")
include("fabric-block-view-api-v2")
include("fabric-renderer-api-v1")
include("fabric-rendering-fluids-v1")

include("deprecated")
//include 'deprecated:fabric-command-api-v1'
//include 'deprecated:fabric-commands-v0'
//include 'deprecated:fabric-containers-v0'
//include 'deprecated:fabric-events-lifecycle-v0'
//include 'deprecated:fabric-keybindings-v0'
//include 'deprecated:fabric-models-v0'
//include 'deprecated:fabric-renderer-registries-v1'
include("deprecated:fabric-rendering-data-attachment-v1")
//include 'deprecated:fabric-rendering-v0'
