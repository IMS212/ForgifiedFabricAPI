import net.fabricmc.loom.api.LoomGradleExtensionAPI

val versionMc: String by rootProject
val versionForge: String by rootProject
val versionForgifiedFabricLoader: String by rootProject
val versionFabricLoader: String by rootProject

val loom = extensions.getByType<LoomGradleExtensionAPI>()
val sourceSets = extensions.getByType<SourceSetContainer>()

val mainSourceSet = sourceSets.getByName("main")

mainSourceSet.apply {
    java {
        srcDir("src/client/java")
    }
    resources {
        srcDir("src/client/resources")
    }
}

dependencies {
    "implementation"("org.sinytra:forgified-fabric-loader:$versionForgifiedFabricLoader")

    "testImplementation"("org.mockito:mockito-core:5.4.0")
    "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.8.1")
    "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks {
    afterEvaluate { 
        named<Jar>("jar") {
            manifest { 
                attributes(
                    "Implementation-Version" to project.version
                )
            }
        }
    }

    named<Test>("test") {
        useJUnitPlatform()
        enabled = false
    }

    named<ProcessResources>("processResources") {
        filesMatching("assets/*/icon.png") {
            exclude()
            rootProject.file("src/main/resources/assets/fabric/icon.png").copyTo(destinationDir.resolve(path))
        }
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(mapOf("version" to version))
        }
    }
}

loom.apply {
    runtimeOnlyLog4j = true

    runs {
        configureEach {
            isIdeConfigGenerated = project.rootProject == project
            property("mixin.debug", "true")
        }
    }
}

