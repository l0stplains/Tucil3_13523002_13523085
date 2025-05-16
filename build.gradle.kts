plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "stima.tucil3"
version = "1.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("tucil_3_stima.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

javafx {
    version = "23.0.2"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.media", "javafx.swing")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.openjfx:javafx-controls:23")
    implementation("org.openjfx:javafx-media:23")
    implementation("org.openjfx:javafx-swing:23")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "tucil_3_stima.Main"
        )
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    // Handle duplicate files
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/java"))
        }
        resources {
            setSrcDirs(listOf("src/resources"))
        }
    }
    test {
        java {
            setSrcDirs(listOf("test/java"))
        }
        resources {
            setSrcDirs(listOf("test/resources"))
        }
    }
}