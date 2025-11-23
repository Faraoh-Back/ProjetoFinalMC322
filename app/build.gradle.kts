plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("java")
}

group = "org.chess"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
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
    // Spring Boot Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // Spring Boot Thymeleaf (for templates if needed)
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    
    // Spring Boot JSON
    implementation("org.springframework.boot:spring-boot-starter-json")
    
    // Spring Boot Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    
    // Google Guava - ESSENCIAL para o código do xadrez
    implementation("com.google.guava:guava:32.1.3-jre")
    
    // Compile only dependencies
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
