plugins {
  java
  kotlin("jvm")
  kotlin("plugin.spring")
  id("io.franzbecker.gradle-lombok")
  id("com.github.ben-manes.versions")
}

tasks.withType(Wrapper::class.java) {
  val gradleWrapperVersion: String by project
  gradleVersion = gradleWrapperVersion
  distributionType = Wrapper.DistributionType.BIN
}

val lombokVersion: String by project
val javaVersion = JavaVersion.VERSION_1_8

allprojects {
  apply(plugin = "java")
  java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
  }

  apply(plugin = "io.franzbecker.gradle-lombok")
  lombok {
    version = lombokVersion
  }

  apply(plugin = "kotlin")
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
      freeCompilerArgs += "-Xjsr305=strict"
      jvmTarget = "$javaVersion"
    }
  }

  repositories {
    mavenCentral()
  }

  val junit4Version: String by project
  val assertkVersion: String by project
  val assertjVersion: String by project
  val junitJupiterVersion: String by project

  dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testImplementation("junit:junit:$junit4Version")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
    testImplementation(platform("org.junit:junit-bom:$junitJupiterVersion"))
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertkVersion")
  }
  tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
      showExceptions = true
      showStandardStreams = true
      events(
          org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
          org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
          org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
      )
    }
  }
}

defaultTasks("clean", "fatJar", "installDist", "distZip", "distTar", "test")
