// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
   dependencies {
      classpath("com.google.dagger:hilt-android-gradle-plugin:2.49")
   }
}

plugins {
   alias(libs.plugins.android.application) apply false
   alias(libs.plugins.kotlin.android) apply false
   id("com.google.devtools.ksp") version "1.9.20-1.0.13" apply false
}