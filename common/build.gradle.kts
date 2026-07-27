plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "org.joebobilly.appleattack"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.adventure)
    testImplementation(kotlin("test"))
}

val targetJavaVersion = 25
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.test {
    useJUnitPlatform()
}