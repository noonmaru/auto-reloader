plugins {
    kotlin("jvm") version "1.4.21"
}

repositories {
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
    maven(url = "https://repo.dmulloy2.net/nexus/repository/public/")
    maven(url = "https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
    implementation("com.destroystokyo.paper:paper-api:1.13.2-R0.1-SNAPSHOT")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
    }
    jar {
        archiveBaseName.set(project.property("pluginName").toString())
        archiveVersion.set("") // For bukkit plugin update
    }
    create<Copy>("paper") {
        from(jar)
        var dest = file(".paper/plugins")
        // if plugin.jar exists in plugins change dest to plugins/update
        if (File(dest, jar.get().archiveFileName.get()).exists()) dest = File(dest, "update")
        into(dest)
    }
}