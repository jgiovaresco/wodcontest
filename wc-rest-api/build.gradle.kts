import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("fr.wc.kotlin-application-conventions")
    `java-test-fixtures`
    alias(libs.plugins.springboot)
    alias(libs.plugins.springdependencymanagement)
    alias(libs.plugins.springplugin)
}

dependencies {
    implementation(project(":wc-core"))

    implementation(platform(libs.arrowkt.bom))
    implementation(libs.bundles.arrowkt)
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation(testFixtures(project(":wc-core")))
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.bundles.restassured)
    testImplementation(libs.bundles.strikt)

    testFixturesImplementation(libs.bundles.arrowkt)
    testFixturesImplementation(libs.faker)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
