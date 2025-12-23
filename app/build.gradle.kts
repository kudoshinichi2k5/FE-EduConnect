plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.doan"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.doan"
        minSdk = 24
        targetSdk = 36
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
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.firebase:firebase-auth:22.3.1")
// Thư viện Firestore (Lưu trữ dữ liệu người dùng)
    implementation("com.google.firebase:firebase-firestore:24.10.2")
// Thư viện Google Sign In (Để đăng nhập bằng Gmail)
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    // Thư viện load ảnh
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    // Thư viện ảnh tròn (tùy chọn, nếu không có thì dùng CardView bo tròn)
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation("io.noties.markwon:core:4.6.2")
}