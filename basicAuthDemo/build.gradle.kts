plugins {
}

group = "com.dmrhimali.auth.basicAuth"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    //    implementation(project(":noAuthDemo"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("com.h2database:h2")  // in-memory database for simplicity
    implementation("org.springframework.boot:spring-boot-starter-json")  // For JSON processing
}

