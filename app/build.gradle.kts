plugins {
    id("com.android.application")
    // Google services plugin for Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.cscorner.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cscorner.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // AndroidX + Material
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Import the Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")

    // Firebase Authentication (for register/login)
    implementation("com.google.firebase:firebase-auth")

    // Firebase Firestore (to store user data)
    implementation("com.google.firebase:firebase-firestore")

    // (Optional) Firebase Storage if you plan to upload files/images
    implementation("com.google.firebase:firebase-storage")

    implementation("com.google.android.material:material:1.11.0")

    implementation("com.google.firebase:firebase-database")

    implementation("androidx.recyclerview:recyclerview:1.3.1")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")


}

