import info.preva1l.trashcan.paper

dependencies {
    paper("1.19.3-R0.1-SNAPSHOT")
}

tasks.register("publishApi") {
    dependsOn("publishMavenJavaPublicationToFinallyADecentRepository")
}