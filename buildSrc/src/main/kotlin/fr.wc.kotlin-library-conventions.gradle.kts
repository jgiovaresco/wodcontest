plugins {
    id("fr.wc.kotlin-common-conventions")

    `java-library`
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
