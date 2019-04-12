import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  java
  kotlin("jvm") version "1.3.30"
  kotlin("plugin.spring") version "1.3.30"
  id("io.franzbecker.gradle-lombok") version "2.1"
}

tasks.withType(Wrapper::class.java) {
  val gradleWrapperVersion: String by project
  gradleVersion = gradleWrapperVersion
  distributionType = Wrapper.DistributionType.BIN
}

val lombokVersion: String by project

lombok {
  version = lombokVersion
}

val javaVersion = JavaVersion.VERSION_1_8

java {
  sourceCompatibility = javaVersion
  targetCompatibility = javaVersion
}

allprojects {
  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      freeCompilerArgs += "-Xjsr305=strict"
      jvmTarget = "$javaVersion"
    }
  }

  repositories {
    mavenCentral()
  }

  tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
      showExceptions = true
      showStandardStreams = true
      events(PASSED, SKIPPED, FAILED)
    }
  }

  apply(plugin = "java")

  val junit4Version: String by project
  val assertkVersion: String by project
  val assertjVersion: String by project
  val junitJupiterVersion: String by project

  dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testImplementation(platform("org.junit:junit-bom:$junitJupiterVersion"))
    testImplementation("junit:junit:$junit4Version")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertkVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
    testRuntime("org.junit.platform:junit-platform-launcher")
  }
}

defaultTasks("clean", "sources", "fatJar", "installDist", "distZip", "distTar", "test")
