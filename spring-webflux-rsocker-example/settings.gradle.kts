pluginManagement {
  repositories {
    gradlePluginPortal()
    maven { url = uri("https://plugins.gradle.org/m2/") }
    maven { url = uri("https://repo.spring.io/milestone/") }
    //maven { url = uri("https://repo.spring.io/snapshot/") }
    mavenCentral()
  }
  plugins {
    id("io.franzbecker.gradle-lombok") version "3.2.0"
    id("com.github.ben-manes.versions") version "0.27.0"
    id("org.springframework.boot") version "2.2.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
  }
}
include(
    "rsocker-server",
    "gateway-client"
)
