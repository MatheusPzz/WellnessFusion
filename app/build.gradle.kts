plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.wellnessfusionapp"
    compileSdk = 34
    buildFeatures {
        compose = true
    }
    defaultConfig {
        applicationId = "com.example.wellnessfusionapp"
        minSdk = 24
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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "34.0.0"
}

dependencies {

    implementation ("com.github.AnyChart:AnyChart-Android:1.1.5")
    implementation ("com.github.tehras:charts:0.2.4-alpha")
    implementation ("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.exyte:animated-navigation-bar:1.0.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation ("com.google.accompanist:accompanist-pager:0.20.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.4")
    implementation ("io.coil-kt:coil-compose:2.5.0")
    implementation ("io.coil-kt:coil-gif:2.0.0-rc02")
    implementation ("com.google.dagger:hilt-android:2.38.1")
    implementation ("com.google.firebase:firebase-firestore-ktx:latest_version")
    implementation("androidx.compose.ui:ui:1.6.4")
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation ("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-analytics")
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation ("androidx.compose.material3:material3:1.3.0-alpha03")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.test.espresso:espresso-web:3.5.1")
    implementation("com.google.firebase:firebase-database-ktx:20.3.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
