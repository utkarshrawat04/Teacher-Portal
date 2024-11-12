plugins {
    id("com.google.gms.google-services")
    id("com.android.application")

}


android {
    namespace = "com.example.portalc"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.portalc"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.recyclerview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("androidx.recyclerview:recyclerview:1.2.1")

    implementation("com.google.android.material:material:1.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("com.google.firebase:firebase-storage:20.0.0")
    implementation("com.google.firebase:firebase-auth:21.0.0")





}