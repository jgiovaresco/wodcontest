plugins {
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }

    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}
