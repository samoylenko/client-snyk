import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

group = "dev.samoylenko"
version = "0.1.2"

repositories {
    mavenCentral()

    maven {
        name = "Central Snapshots"
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
    }
}

plugins {
    signing

    alias(libs.plugins.kotlin.plugin.multiplatform)
    alias(libs.plugins.kotlin.plugin.serialization)

    alias(libs.plugins.maven.publish)
}

kotlin {
    explicitApi()

    withSourcesJar(publish = true)

    js {
        nodejs {
            testTask {
                useMocha {
                    // Long API paged queries
                    timeout = "5m"
                }
            }
        }
    }

    jvm {}

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)
            api(libs.kotlinx.serialization.json)

            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.kotlin.logging)
            implementation(libs.util.platform)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
        }

        jvmTest.dependencies {
            implementation(libs.logback.classic)
        }

        jsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}

signing {
    useGpgCmd()
}

mavenPublishing {
    signAllPublications()

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    pom {
        name = "Snyk Client - Kotlin Multiplatform"
        description = "A Snyk REST API Client library (Kotlin Multiplatform)"
        url = "https://github.com/samoylenko/client-snyk"

        licenses {
            license {
                name = "The MIT License"
                url = "https://github.com/samoylenko/client-snyk/blob/main/LICENSE"
            }
        }

        developers {
            developer {
                id = "samoylenko"
                name = "Michael Samoylenko"
                url = "https://github.com/samoylenko"
            }
        }

        scm {
            url = "https://github.com/samoylenko/client-snyk"
            connection = "scm:git:git://github.com/samoylenko/client-snyk.git"
            developerConnection = "scm:git:ssh://git@github.com:samoylenko/client-snyk.git"
        }
    }
}

rootProject.plugins.withType(YarnPlugin::class.java) {
    rootProject.the<YarnRootExtension>().ignoreScripts = true
    rootProject.the<YarnRootExtension>().yarnLockAutoReplace = true
    rootProject.the<YarnRootExtension>().yarnLockMismatchReport = YarnLockMismatchReport.WARNING
}
