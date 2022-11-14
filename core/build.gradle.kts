plugins {
    id("fr.wc.kotlin-library-conventions")
    `java-test-fixtures`
}

dependencies {
    implementation(platform(libs.arrowkt.bom))
    implementation(libs.bundles.arrowkt)

    testImplementation(libs.bundles.kotest)
    testImplementation(libs.bundles.strikt)

    testFixturesImplementation(libs.bundles.arrowkt)
    testFixturesImplementation(libs.faker)
}
