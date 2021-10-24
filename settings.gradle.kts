rootProject.name = "InsnReplacer"

pluginManagement {
  plugins {
    val indraVersion: String by settings
    val kotlinVersion: String by settings
    kotlin("jvm") version kotlinVersion
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.publishing.sonatype") version indraVersion
  }
}
