import java.io.FileOutputStream

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("com.github.johnrengelman.shadow") version ("8.1.1")
}

group = "com.laudynetwork.manhunt"
version = "1.0-SNAPSHOT"
description = "LaudyNetwork Game Manhunt"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io/")
    maven("https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local/")
    maven("https://eldonexus.de/repository/maven-proxies/")
    maven("https://repo.ranull.com/maven/external")
    maven {
        url = uri("https://repo.laudynetwork.com/repository/maven")
        authentication {
            create<BasicAuthentication>("basic")
        }
        credentials {
            username = System.getenv("NEXUS_USER")
            password = System.getenv("NEXUS_PWD")
        }
    }
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    paperweight.paperDevBundle("1.20-R0.1-SNAPSHOT")

    compileOnly("com.laudynetwork:networkutils:latest") {
        exclude(group = "eu.thesimplecloud.simplecloud", module = "simplecloud-api")
        exclude(group = "com.laudynetwork", module = "database")
        exclude(group = "org.mongodb", module = "mongodb-driver-sync")
    }

    compileOnly("org.mongodb:mongodb-driver-sync:4.10.1")
    compileOnly("com.laudynetwork:database:latest")
    compileOnly("eu.thesimplecloud.simplecloud:simplecloud-api:2.5.0")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT")
}
tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    shadowJar {
        dependencies {
            exclude(dependency("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT"))
//            exclude(dependency("eu.thesimplecloud.simplecloud:simplecloud-api:2.5.0"))
//            exclude(dependency("com.laudynetwork:networkutils:latest"))
//            exclude(dependency("com.laudynetwork:database:latest"))
//            exclude(dependency("dev.sergiferry:playernpc:2023.4"))
//            exclude(dependency("eu.thesimplecloud.clientserverapi:clientserverapi:4.1.17"))
//            exclude(dependency("eu.thesimplecloud.jsonlib:json-lib:1.0.10"))
//            exclude(dependency("eu.thesimplecloud.simplecloud:simplecloud-runner:2.5.0"))
        }
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    reobfJar {
        // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
        // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
        outputJar.set(layout.buildDirectory.file("dist/Manhunt.jar"))
    }
}

// Uncommit to activate translations

//tasks.register("translations") {
//    downloadFile(System.getenv("TOLGEE_TOKEN_PLUGIN"), "own")
//    downloadFile(System.getenv("TOLGEE_TOKEN_GENERAL"), "plugins")
//}

fun downloadFile(token: String, dir: String) {
    downloadLink(token).forEach {
        downloadFromServer(it.key, it.value + ".json", dir)
    }
}

fun downloadFromServer(url: String, fileName: String, dir: String) {
    file("${projectDir}/src/main/resources/translations/${dir}").mkdirs()
    val f = file("${projectDir}/src/main/resources/translations/${dir}/${fileName}")
    uri(url).toURL().openStream().use {
        it.copyTo(
                FileOutputStream(f)
        )
    }
}

fun downloadLink(token: String): Map<String, String> {
    val map = HashMap<String, String>()
    val params = "format=JSON&zip=false&structureDelimiter"
    map["https://tolgee.laudynetwork.com/v2/projects/export?languages=en&$params&ak=$token"] = "en"
    map["https://tolgee.laudynetwork.com/v2/projects/export?languages=de&$params&ak=$token"] = "de"
    return map
}