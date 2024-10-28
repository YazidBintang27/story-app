plugins {
   alias(libs.plugins.android.application)
   alias(libs.plugins.kotlin.android)
   id("com.google.devtools.ksp")
   id("com.google.dagger.hilt.android")
}

android {
   namespace = "com.latihan.storyou"
   compileSdk = 34

   defaultConfig {
      applicationId = "com.latihan.storyou"
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
   kotlinOptions {
      jvmTarget = "1.8"
   }
   buildFeatures {
      viewBinding = true
   }
}

dependencies {

   implementation(libs.androidx.core.ktx)
   implementation(libs.androidx.appcompat)
   implementation(libs.material)
   implementation(libs.androidx.activity)
   implementation(libs.androidx.constraintlayout)
   testImplementation(libs.junit)
   androidTestImplementation(libs.androidx.junit)
   androidTestImplementation(libs.androidx.espresso.core)
   implementation("androidx.activity:activity-ktx:1.9.3")

   // Dagger Hilt
   implementation("com.google.dagger:hilt-android:2.49")
   ksp("com.google.dagger:dagger-compiler:2.49")
   ksp("com.google.dagger:hilt-compiler:2.49")

   //SplashScreen
   implementation ("androidx.core:core-splashscreen:1.0.1")

   // Retrofit
   implementation("com.squareup.retrofit2:retrofit:2.9.0")
   implementation("com.squareup.retrofit2:converter-gson:2.9.0")

   // OkHttp and Chucker
   implementation("com.squareup.okhttp3:okhttp:4.12.0")
   debugImplementation("com.github.chuckerteam.chucker:library:4.0.0")
   releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.0.0")

   // Coroutines
   implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

   // Lifecycle
   implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
   implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
   implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

   // Glide
   implementation("com.github.bumptech.glide:glide:4.16.0")
   annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")
}