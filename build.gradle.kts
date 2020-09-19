plugins {
    kotlin("jvm") version "1.4.10"
}

repositories {
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
    maven(url = "https://repo.dmulloy2.net/nexus/repository/public/")
    maven(url = "https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("com.destroystokyo.paper:paper-api:1.13.2-R0.1-SNAPSHOT")

    testImplementation("junit:junit:4.12")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    javadoc {
        options.encoding = "UTF-8"
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
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
    create<Copy>("copyJarToDocker") {
        from(jar)

        val jarTask = jar.get()
        val fileName = jarTask.archiveFileName.get()
        var dest = File(".docker/plugins")
        // Copy bukkit plugin update folder
        if (File(dest, fileName).exists()) dest = File(dest, "update")

        into(dest)

        doLast {
            println("Copy from $fileName to ${dest.path}")
        }
    }
}