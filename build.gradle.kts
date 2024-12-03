import me.modmuss50.mpp.ReleaseType
import net.fabricmc.loom.build.nesting.JarNester
import net.fabricmc.loom.build.nesting.NestableJarGenerationTask
import net.fabricmc.loom.util.Constants
import org.apache.commons.codec.digest.DigestUtils
import org.eclipse.jgit.api.Git

plugins {
    java
    `maven-publish`
    id("dev.architectury.loom") // Version declared in buildSrc
    id("me.modmuss50.mod-publish-plugin") version "0.5.+"
}

val implementationVersion: String by project
val versionMc: String by project
val versionForge: String by project
val versionForgifiedFabricLoader: String by project

val curseForgeId: String by project
val modrinthId: String by project
val githubRepository: String by project
val publishBranch: String by project

val META_PROJECTS: List<String> = listOf(
    "deprecated",
    "fabric-api-bom",
    "fabric-api-catalog"
)
val DEV_ONLY_MODULES: List<String> = listOf(
    "fabric-gametest-api-v1"
)

ext["getSubprojectVersion"] = object : groovy.lang.Closure<Unit>(this) {
    fun doCall(project: Project): String {
        return getSubprojectVersion(project)
    }
}
ext["moduleDependencies"] = object : groovy.lang.Closure<Unit>(this) {
    fun doCall(project: Project, depNames: List<String>) {
        moduleDependencies(project, depNames)
    }
}
ext["testDependencies"] = object : groovy.lang.Closure<Unit>(this) {
    fun doCall(project: Project, depNames: List<String>) {
        testDependencies(project, depNames)
    }
}

val upstreamVersion = version

ext["upstreamVersion"] = upstreamVersion

version = "$upstreamVersion+$implementationVersion+$versionMc${(if (System.getenv("GITHUB_RUN_NUMBER") == null) "+local" else "")}"
println("Version: $version")

allprojects {
    apply(plugin = "maven-publish")

    publishing {
        repositories {
            val env = System.getenv()
            if (env["MAVEN_URL"] != null) {
                repositories.maven {
                    url = uri(env["MAVEN_URL"] as String)
                    if (env["MAVEN_USERNAME"] != null) {
                        credentials {
                            username = env["MAVEN_USERNAME"]
                            password = env["MAVEN_PASSWORD"]
                        }
                    }
                }
            }
        }
    }

    group = "org.sinytra.forgified-fabric-api"

    if (project.name in META_PROJECTS) {
        return@allprojects
    }

    apply(plugin = "java-library")
    apply(plugin = "dev.architectury.loom")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        withSourcesJar()
    }

    repositories {
        mavenCentral()
        maven {
            name = "FabricMC"
            url = uri("https://maven.fabricmc.net")
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
            name = "Sinytra"
            url = uri("https://maven.su5ed.dev/releases")
        }
        mavenLocal()
    }

    dependencies {
        minecraft(group = "com.mojang", name = "minecraft", version = versionMc)
        neoForge(group = "net.neoforged", name = "neoforge", version = versionForge)
        mappings(loom.layered {
            officialMojangMappings {
                nameSyntheticMembers = true
            }
        })
    }

    // Run this task after updating minecraft to regenerate any required resources
    tasks.register("generateResources") {

    }
}

dependencies {
    // Include Forgified Fabric Loader
    include("org.sinytra:forgified-fabric-loader:$versionForgifiedFabricLoader:full")
    api("org.sinytra:forgified-fabric-loader:$versionForgifiedFabricLoader")
}

tasks {
    named<Jar>("jar") {
        doLast {

        }
    }

    withType<JavaCompile> {
        options.release = 21
    }
}

// Subprojects

allprojects {
    if (project.name in META_PROJECTS) {
        return@allprojects
    }

    val modDependencies: Configuration by configurations.creating

    tasks.register("generate") {
        group = "sinytra"
    }

    // Setup must come before generators
    apply(plugin = "ffapi.neo-setup")
    apply(plugin = "ffapi.neo-conversion")
    apply(plugin = "ffapi.neo-entrypoint")
    apply(plugin = "ffapi.package-info")

    allprojects.forEach { p ->
        if (!META_PROJECTS.contains(p.name)) {
            loom.mods.register(p.name) {
                sourceSet(p.sourceSets.main.get())
            }
    
            if (p.file("src/testmod").exists() || p.file("src/testmodClient").exists()) {
                loom.mods.register(p.name + "-testmod") {
                    sourceSet(p.sourceSets.getByName("testmod"))
                }
            }
        }
    }

    tasks.named<Jar>("sourcesJar") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    publishing {
        publications {
            register<MavenPublication>("mavenJava") {
                from(components["java"])
            }
        }
    }

    if (project != rootProject) {
        configurations {
            apiElements {
                setExtendsFrom(emptySet())
            }
            runtimeElements {
                setExtendsFrom(emptySet())
            }
        }
    }
}

publishMods {
    file.set(tasks.jar.flatMap { it.archiveFile })
    changelog.set(providers.environmentVariable("CHANGELOG").orElse("# ${project.version}"))
    type.set(providers.environmentVariable("PUBLISH_RELEASE_TYPE").orElse("alpha").map(ReleaseType::of))
    modLoaders.add("neoforge")
    dryRun.set(!providers.environmentVariable("CI").isPresent)
    displayName.set("[$versionMc] Forgified Fabric API ${project.version}")

    github {
        accessToken.set(providers.environmentVariable("GITHUB_TOKEN"))
        repository.set(githubRepository)
        commitish.set(publishBranch)
    }
    curseforge {
        accessToken.set(providers.environmentVariable("CURSEFORGE_TOKEN"))
        projectId.set(curseForgeId)
        minecraftVersions.add(versionMc)
    }
    modrinth {
        accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
        projectId.set(modrinthId)
        minecraftVersions.add(versionMc)
    }
}

dependencies {
	afterEvaluate {
		subprojects.forEach { proj ->
			if (proj.name in META_PROJECTS) {
				return@forEach
			}

			include(api(project(proj.path, "namedElements"))!!)
			"testmodImplementation"(proj.sourceSets.getByName("testmod").output)
		}
	}
}

val git: Git? = runCatching { Git.open(rootDir) }.getOrNull()

fun getSubprojectVersion(project: Project): String {
    // Get the version from the gradle.properties file
    val version = properties["${project.name}-version"] as? String
        ?: throw NullPointerException("Could not find version for " + project.name)

    if (git == null) {
        return "$version+nogit"
    }

    val latestCommits = git.log().addPath(project.name).setMaxCount(1).call().toList()
    if (latestCommits.isEmpty()) {
        return "$version+uncommited"
    }

    return version + "+" + latestCommits[0].id.name.substring(0, 8) + DigestUtils.sha256Hex(versionMc).substring(0, 2)
}

fun moduleDependencies(project: Project, depNames: List<String>) {
    val deps = depNames.map { project.dependencies.project(":$it", "namedElements") }

    project.dependencies {
        deps.forEach {
            api(it)
            implementation(it)
        }
    }
}

fun testDependencies(project: Project, depNames: List<String>) {
    val deps = depNames.map { project.dependencies.project(":$it", "namedElements") }

    project.dependencies {
        deps.forEach {
            "testmodImplementation"(it)
        }
    }
}
