plugins {
  `java-library`
  kotlin("jvm")
  `maven-publish`
  signing
}

group = "org.sourcegrade"
version = "0.1.0-SNAPSHOT"

repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  compileOnlyApi("org.sourcegrade:jagr-grader-api:0.1")
  compileOnlyApi(kotlin("reflect"))
}

java {
  withSourcesJar()
  withJavadocJar()
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
      artifactId = "insn-replacer"
    }
  }
}
