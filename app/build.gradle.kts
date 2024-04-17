import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
    kotlin("android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    val c: Calendar = Calendar.getInstance()
    val df = SimpleDateFormat("dd-MMM-yyyy HH-mm-ss a", Locale.US)
    val buildDate = df.format(c.time)

    namespace = "com.example.chatappstarting"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chatappstarting"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isDebuggable = true
        }

        create("DEV") {
            initWith(getByName("debug"))
        }

        buildOutputs.all {
            val variantOutputImpl =
                this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val variantName: String = variantOutputImpl.name
            val outputFileName =
                "[$variantName]LetsChat - ${defaultConfig.versionName}-(${defaultConfig.versionCode})-$buildDate-.apk"
            variantOutputImpl.outputFileName = outputFileName
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    //firestore
    implementation("com.google.firebase:firebase-firestore:24.10.0")

    //Splash Api
    implementation("androidx.core:core-splashscreen:1.0.1")

    //Datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.46.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // ... Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-rxjava2:2.6.1")

    //Ktor
    implementation("io.ktor:ktor-client-core:2.3.1")
    implementation("io.ktor:ktor-client-android:2.3.1")
    implementation("io.ktor:ktor-client-gson:2.3.1")
    implementation("io.ktor:ktor-serialization-gson:2.3.1")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.1")

    //firebaseAuth
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth:22.3.0")

    //firebaseCore
    implementation("com.google.firebase:firebase-analytics")
}