plugins {
    id("java")
    application
}

group = "ro.ubb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.7")
}

// Îi spunem lui Gradle că fișierele .java și config.properties sunt direct în folderul "src"
sourceSets {
    main {
        java {
            setSrcDirs(listOf("src"))
        }
        resources {
            setSrcDirs(listOf("src"))
            include("**/*.properties")
        }
    }
}

application {
    mainClass.set("Main")
}

// Permite citirea de la tastatură (Scanner) în consolă când rulezi cu Gradle
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}