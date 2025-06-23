import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import info.preva1l.trashcan.finallyADecent
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    java
    `maven-publish`
    id("org.ajoberstar.grgit") version "5.3.0"
    id("com.gradleup.shadow") version "8.3.6"

    id("info.preva1l.trashcan") version "1.0.0"
}

var currentBranch: String = grgit.branch.current().name
val devMode = currentBranch != "master" && currentBranch != "HEAD"
if (devMode) {
    println("Starting in development mode")
}

allprojects {
    group = "info.preva1l.fadah"
    version = "3.2.0"

    repositories {
        mavenCentral()
        finallyADecent(dev = devMode)
        finallyADecent()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "info.preva1l.trashcan")

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.38")
        annotationProcessor("org.projectlombok:lombok:1.18.38")

        compileOnly("org.jetbrains:annotations:26.0.2")
        annotationProcessor("org.jetbrains:annotations:26.0.2")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.2")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.12.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.12.2")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.12.2")

        testCompileOnly("org.jetbrains:annotations:26.0.2")
        testCompileOnly("org.projectlombok:lombok:1.18.38")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.38")
    }

    tasks.withType<ShadowJar> {
        destinationDirectory.set(file("$rootDir/target"))
        archiveFileName.set("${rootProject.name}-${project.name.uppercaseFirstChar()}-$version.jar")
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
        options.isFork = true
        options.encoding = "UTF-8"
    }

    tasks.register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    tasks.withType<Javadoc> {
        (options as StandardJavadocDocletOptions).tags(
            "apiNote:a:API Note:",
            "implSpec:a:Implementation Requirements:",
            "implNote:a:Implementation Note:"
        )
    }

    tasks.register<Jar>("javadocJar") {
        dependsOn("javadoc")
        archiveClassifier.set("javadoc")
        from(tasks.named<Javadoc>("javadoc").get().destinationDir)
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    publishing {
        repositories.finallyADecent(dev = devMode, authenticated = true)

        publications {
            register(
                name = "mavenJava",
                type = MavenPublication::class,
                configurationAction = {
                    from(components["java"])

                    artifact(tasks.named("sourcesJar"))
                    artifact(tasks.named("javadocJar"))
                }
            )
        }
    }

    tasks.getByName("build")
        .dependsOn(
            "shadowJar"
        )
}

project.delete("$rootDir/target")
logger.lifecycle("Building Fadah $version")