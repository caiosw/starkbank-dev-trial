import java.util.Properties

val properties = Properties()
file("local.properties").inputStream().use { properties.load(it) }

group = "com.starkbank.devtrial"
version = "0.0.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.microsoft.azure.functions:azure-functions-java-library:3.1.0")
    implementation("com.starkbank:sdk:2.19.0")
    implementation("com.azure:azure-security-keyvault-secrets:4.8.7")
    implementation("com.azure:azure-identity:1.14.0")

    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}

plugins {
    kotlin("jvm") version "1.9.22"
    id("com.microsoft.azure.azurefunctions") version "1.16.0"
    id("jacoco")
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

jacoco {
    toolVersion = "0.8.7"
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    additionalEditorconfigFile.set(file(".editorconfig"))
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required = false
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    finalizedBy(tasks.jacocoTestReport)
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
    options.encoding = "UTF-8"
}

sourceSets {
    main {
        java {
            // this is needed, otherwise the :azureFunctionPackage will look for build/classes/java/main
            destinationDirectory.set(File("build/classes/kotlin/main"))
        }
    }
}

azurefunctions {
    subscription = properties.getProperty("AZURE_SUBSCRIPTION")
    resourceGroup = "dev-starkbank-rg"
    appName = "dev-starkbank-devtrial-azf"
    pricingTier = "Consumption"
    region = "westus2"
    allowTelemetry = false

    setRuntime(
        closureOf<com.microsoft.azure.gradle.configuration.GradleRuntimeConfig> {
            os("Linux")
            javaVersion("Java 11")
        }
    )

    setAppSettings(
        closureOf<MutableMap<String, String>> {
            put("SERVICE_BUS_CONN_STRING", "@Microsoft.KeyVault(VaultName=dev-starkbank-kv;SecretName=dev-starkbank-devtrial-sbq)")
            put("STARK_BANK_API_SECRET_KEY", "@Microsoft.KeyVault(VaultName=dev-starkbank-kv;SecretName=dev-starkbank-api-private-key)")
        }
    )

    setAuth(
        closureOf<com.microsoft.azure.gradle.auth.GradleAuthConfig> {
            type = "azure_cli"
        }
    )

    localDebug = "transport=dt_socket,server=y,suspend=n,address=5005"

    setDeployment(
        closureOf<com.microsoft.azure.plugin.functions.gradle.configuration.deploy.Deployment> {
            type = "run_from_blob"
        }
    )
}
