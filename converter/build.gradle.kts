plugins {
    kotlin("jvm")
    application
}

group = "com.sparkedember"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(libs.kotlin.compiler.embeddable)
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("TestToolbarLayoutKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
