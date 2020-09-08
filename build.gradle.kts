import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val junitVersion = "5.6.2"

plugins {
    kotlin("jvm") version "1.4.0"
}
group = "co.fun"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation("net.sourceforge.tess4j:tess4j:4.5.3")
    implementation("org.openpnp:opencv:4.3.0-2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions {
        jvmTarget = "13"
    }
}
