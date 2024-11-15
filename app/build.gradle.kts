import java.util.Properties

plugins {
   alias(libs.plugins.android.application)
   alias(libs.plugins.kotlin.android)
   id("com.google.devtools.ksp")
   id("com.google.dagger.hilt.android")
   id("androidx.navigation.safeargs.kotlin")
}

android {
   namespace = "com.latihan.storyou"
   compileSdk = 35

   defaultConfig {
      applicationId = "com.latihan.storyou"
      minSdk = 24
      //noinspection EditedTargetSdkVersion
      targetSdk = 35
      versionCode = 1
      versionName = "1.0"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      val properties = Properties()
      properties.load(project.rootProject.file("local.properties").inputStream())
      buildConfigField("String", "MAPS_API_KEY", properties.getProperty("MAPS_API_KEY"))
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
      buildConfig = true
   }
}

dependencies {

   implementation(libs.androidx.core.ktx)
   implementation(libs.androidx.appcompat)
   implementation(libs.material)
   implementation(libs.androidx.activity)
   implementation(libs.androidx.constraintlayout)
   testImplementation(libs.junit)
   testImplementation(libs.hilt.android.testing)
   androidTestImplementation(libs.androidx.junit)
   androidTestImplementation(libs.androidx.espresso.core)
   implementation(libs.androidx.activity.ktx)

   // Dagger Hilt
   implementation(libs.hilt.android)
   ksp(libs.dagger.compiler)
   ksp(libs.hilt.compiler)
   androidTestImplementation(libs.hilt.android.testing)

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

   // Jetpack Navigation
   implementation(libs.androidx.navigation.fragment.ktx)
   implementation(libs.androidx.navigation.ui.ktx)

   // Datastore
   implementation(libs.androidx.datastore.preferences)

   // CameraX
   implementation(libs.androidx.camera.camera2)
   implementation(libs.androidx.camera.lifecycle)
   implementation(libs.androidx.camera.view)

   // Google Maps and Location
   implementation(libs.play.services.maps)
   implementation(libs.play.services.location)

   // Paging 3 and Room Database
   implementation(libs.androidx.room.runtime)
   ksp(libs.androidx.room.compiler)
   implementation(libs.androidx.room.ktx)
   implementation(libs.androidx.paging.runtime)
   implementation(libs.androidx.room.paging)
   
   // Unit Testing
   testImplementation(libs.androidx.core.testing)
   testImplementation(libs.kotlinx.coroutines.test)
   testImplementation(libs.mockito.core)
   testImplementation(libs.mockito.inline)
   implementation(libs.core.ktx)
}