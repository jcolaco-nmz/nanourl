import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.2.6.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	id("org.jmailen.kotlinter") version "2.1.1"
	kotlin("jvm") version "1.3.71"
	kotlin("plugin.spring") version "1.3.71"
}

group = "com.jcolaco"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("commons-validator:commons-validator:1.6")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
	implementation("org.ehcache:ehcache:3.8.1")
	implementation("javax.cache:cache-api:1.1.1")

	runtimeOnly("org.postgresql:postgresql")

	// Test Dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0")
	testRuntimeOnly("org.postgresql:postgresql")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.register("downloadDependenciesForDocker", Copy::class) {
	mkdir("tmp")
	from(sourceSets.main.get().runtimeClasspath, sourceSets.test.get().runtimeClasspath)
	into("tmp")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks {
	check {
		dependsOn(lintKotlin)
	}
	assemble {
		mustRunAfter(clean)
	}
}
