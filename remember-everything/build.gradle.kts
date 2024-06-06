plugins {
  id("java")
  id("org.springframework.boot") version "3.2.2"
  id("io.spring.dependency-management") version "1.1.4"
}

group = "com.niuma"
version = "0.0.1-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")

  compileOnly("org.projectlombok:lombok")

  runtimeOnly("com.mysql:mysql-connector-j")
  runtimeOnly("com.h2database:h2")

  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  annotationProcessor("org.projectlombok:lombok")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")

  developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks {
  test {
    useJUnitPlatform()
  }
}
