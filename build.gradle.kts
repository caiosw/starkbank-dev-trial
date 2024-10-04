plugins {
    kotlin("jvm") version "1.9.22"
    id("com.microsoft.azure.azurefunctions") version "1.8.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile> {
    options.release.set(11)
}

azurefunctions {
    subscription = "dev-starkbank-subscription"
    resourceGroup = "dev-starkbank-rg"
    appName = "dev-starkbank-azf"
    pricingTier = "Consumption"
    region = "westus2"
    setRuntime(closureOf<com.microsoft.azure.gradle.configuration.GradleRuntimeConfig> {
        os("Linux")
    })
    setAppSettings(closureOf<MutableMap<String, String>> {
        put("key", "value")
    })
    setAuth(closureOf<com.microsoft.azure.gradle.auth.GradleAuthConfig> {
        type = "azure_cli"
    })
    // enable local debug
    // localDebug = "transport=dt_socket,server=y,suspend=n,address=5005"
    setDeployment(closureOf<com.microsoft.azure.plugin.functions.gradle.configuration.deploy.Deployment> {
        type = "run_from_blob"
    })
}