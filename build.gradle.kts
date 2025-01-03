plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.allopen") version "2.0.21"

    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {

    implementation(kotlin("stdlib-jdk8"))

    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    implementation("io.quarkus:quarkus-kotlin")

    implementation("io.quarkus:quarkus-picocli")
    implementation("io.quarkus:quarkus-arc")


    val arcVersion = "0.1.22-SNAPSHOT"

    implementation("org.eclipse.lmos:arc-agents:$arcVersion")
    implementation("org.eclipse.lmos:arc-result:$arcVersion")
    implementation("org.eclipse.lmos:arc-ollama-client:$arcVersion")
    implementation("org.eclipse.lmos:arc-openai-client:$arcVersion")

    implementation("com.openai:openai-java:0.8.0")


    testImplementation("io.quarkus:quarkus-junit5")
}

group = "org.eclipse.lmos"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
        javaParameters = true
    }
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
