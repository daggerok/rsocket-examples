pluginManagement {
  repositories {
    gradlePluginPortal()
    maven { url = uri("https://plugins.gradle.org/m2/") }
    maven { url = uri("https://repo.spring.io/milestone/") }
    //maven { url = uri("https://repo.spring.io/snapshot/") }
    mavenCentral()
  }
  plugins {
    id("io.franzbecker.gradle-lombok") version "3.2.0" apply false
    id("com.github.ben-manes.versions") version "0.25.0" apply false
    id("org.springframework.boot") version "2.2.0.RELEASE" apply false
    id("io.spring.dependency-management") version "1.0.8.RELEASE" apply false
  }
}
include(
    "rsocker-server",
    "gateway-client"
)
