plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.protobuf") version "0.9.4"
}

android {
    namespace = "maxsimus.RemoteCam"
    compileSdk = 34

    packagingOptions {
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/io.netty.versions.properties")
    }

    defaultConfig {
        applicationId = "maxsimus.RemoteCam"
        minSdk = 33
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        renderscriptTargetApi = 18
        renderscriptSupportModeEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                abiFilters("arm64-v8a")
                cppFlags += "-std=c++17"
                arguments += "-DANDROID_STL=c++_shared"
                cppFlags += "-mfloat-abi=hard"
                cppFlags += "-mfpu=neon"
                cppFlags += "-O3"
                cppFlags += "-finline-functions"
                cppFlags += "-fvectorize"
                arguments += "-DANDROID_ARM_NEON=ON"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")

            externalNativeBuild {
                cmake {
                    cppFlags += "-std=c++17"
                    cppFlags += "-mfloat-abi=hard"
                    cppFlags += "-mfpu=neon"
                    cppFlags += "-O3"
                    cppFlags += "-finline-functions"
                    cppFlags += "-fvectorize"
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        prefab = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:3.23.4"
        }
        plugins {
            create("grpc") {
                artifact = "io.grpc:protoc-gen-grpc-java:1.57.1"
            }
            create("grpckt") {
                artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
            }
        }
        generateProtoTasks {
            all().forEach {
                it.plugins {
                    create("grpc")
                    create("grpckt")
                }
                it.builtins {
                    create("java") //needed either it throws Unresolved Reference
                    create("kotlin")
                }
            }
        }

    }
}


buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        // ASSUMES GRADLE 2.12 OR HIGHER. Use plugin version 0.7.5 with earlier
        // gradle versions
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.9.4")
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.01.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Material Design 3
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.graphics:graphics-core:1.0.0-beta01")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.google.api.grpc:proto-google-common-protos:2.17.0")
    implementation("io.grpc:grpc-netty:1.61.1")
    implementation("io.grpc:grpc-netty-shaded:1.61.1")
    implementation("io.grpc:grpc-okhttp:1.61.1")

    implementation("io.grpc:grpc-protobuf:1.57.1")
    implementation("io.grpc:grpc-stub:1.57.2")
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    implementation("io.grpc:grpc-netty:1.61.1")

    implementation("com.google.protobuf:protobuf-kotlin:3.22.3")
    implementation("com.google.protobuf:protobuf-java:3.22.3")

    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}