pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
  val kotlinVersion: String by extra
  val lombokGradlePluginVersion: String by extra
  val versionsGradlePluginVersion: String by extra
  plugins {
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("io.franzbecker.gradle-lombok") version lombokGradlePluginVersion
    id("com.github.ben-manes.versions") version versionsGradlePluginVersion apply false
  }
}

val name: String by extra
rootProject.name = name

include(
    "server",
    "client"
)
