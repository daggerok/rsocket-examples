pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
  val kotlinVersion: String by extra
  plugins {
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("io.franzbecker.gradle-lombok") version "3.2.0"
    id("com.github.ben-manes.versions") version "0.27.0"
  }
}
include(
    "rsocket-java-example"
)
