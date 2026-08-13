plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "org.joebobilly.appleattack"
version = "unspecified"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}

dependencies {
    compileOnly(libs.adventure)
    compileOnly(libs.minestom)
    compileOnly(libs.paper)
    implementation(libs.guava)
    testImplementation(kotlin("test"))
}

val targetJavaVersion = 25
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.test {
    useJUnitPlatform()
}