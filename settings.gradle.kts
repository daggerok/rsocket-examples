pluginManagement {
  repositories {
    gradlePluginPortal()
    maven { url = uri("https://plugins.gradle.org/m2/") }
    maven { url = uri("https://repo.spring.io/milestone/") }
    //maven { url = uri("https://repo.spring.io/snapshot/") }
    mavenCentral()
  }
  resolutionStrategy {
    eachPlugin {
      if (requested.id.id == "org.springframework.boot") {
        useModule("org.springframework.boot:spring-boot-gradle-plugin:${requested.version}")
      }
    }
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
