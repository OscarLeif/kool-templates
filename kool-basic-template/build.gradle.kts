import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform") version "2.2.21"
}

repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots")
}

kotlin {
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        binaries {
            executable {
                mainClass.set("LauncherKt")
                applicationDefaultJvmArgs = buildList {
                    add("--add-opens=java.base/java.lang=ALL-UNNAMED")
                    add("--enable-native-access=ALL-UNNAMED")
                    if (OperatingSystem.current().isMacOsX) {
                        add("-XstartOnFirstThread")
                    }
                }
            }
        }
    }
    jvmToolchain(21)

    js {
        binaries.executable()
        browser {
            @OptIn(ExperimentalDistributionDsl::class)
            distribution {
                outputDirectory.set(File("${rootDir}/dist/js"))
            }
            commonWebpackConfig {
                //mode = KotlinWebpackConfig.Mode.PRODUCTION
                mode = KotlinWebpackConfig.Mode.DEVELOPMENT
            }
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            target.set("es2015")
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                // add additional kotlin multi-platform dependencies here...
                implementation(libs.kool.core)
                implementation(libs.kool.physics)
                implementation(libs.kool.physics2d)
            }
        }

        val jvmMain by getting {
            dependencies {
                // add additional jvm-specific dependencies here...
            }
        }
        
        val jsMain by getting {
            dependencies {
                // add additional js-specific dependencies here...
            }
        }
    }
}

val clean by tasks.getting(Task::class) {
    doLast {
        delete("${rootDir}/dist")
    }
}
