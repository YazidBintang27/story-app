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
   implementation(libs.androidx.activity.ktx)

   // Dagger Hilt
   implementation(libs.hilt.android)
   ksp(libs.dagger.compiler)
   ksp(libs.hilt.compiler)

   //SplashScreen
   implementation (libs.androidx.core.splashscreen)

   // Retrofit
   implementation(libs.retrofit)
   implementation(libs.converter.gson)

   // OkHttp and Chucker
   implementation(libs.okhttp)
   debugImplementation(libs.library)
   releaseImplementation(libs.library.no.op)

   // Coroutines
   implementation (libs.kotlinx.coroutines.android)

   // Lifecycle
   implementation(libs.androidx.lifecycle.viewmodel.ktx)
   implementation(libs.androidx.lifecycle.livedata.ktx)
   implementation(libs.androidx.lifecycle.runtime.ktx)

   // Glide
   implementation(libs.glide)
   annotationProcessor(libs.compiler)
}