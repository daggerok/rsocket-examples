import org.gradle.api.JavaVersion

object Globals {
  object Project {
    const val groupId = "com.github.daggerok"
    const val version = "1.0-SNAPSHOT"
  }

  val javaVersion = JavaVersion.VERSION_1_8
  const val vavrVersion = "0.10.0"
  const val lombokVersion = "1.18.8"
  const val junitVersion = "4.13-beta-3"
  // const val rsocketVersion = "0.12.2-RC3"
  const val rsocketVersion = "1.0.0-RC3"
  const val junitJupiterVersion = "5.5.0-RC1"

  object Gradle {
    const val wrapperVersion = "5.5-rc-2"

    object Plugin {
      const val lombokVersion = "3.1.0"
      const val versionsVersion = "0.25.0"
      const val springBootVersion = "2.2.0.RELEASE"
      const val dependencyManagementVersion = "1.0.8.RELEASE"
    }
  }
}
