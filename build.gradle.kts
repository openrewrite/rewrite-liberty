plugins {
    id("org.openrewrite.build.recipe-library") version "latest.release"
}

group = "org.openrewrite.recipe"
description = "Recipes to migrate to the IBM WebSphere Liberty. Automatically."

val rewriteVersion = rewriteRecipe.rewriteVersion.get()
dependencies {
    implementation(platform("org.openrewrite:rewrite-bom:$rewriteVersion"))
    implementation(platform("org.openrewrite.recipe:rewrite-recipe-bom:$rewriteVersion"))
    implementation("org.openrewrite:rewrite-java")
    implementation("org.openrewrite.recipe:rewrite-java-dependencies")
    implementation("org.openrewrite.recipe:rewrite-migrate-java")
    implementation("org.openrewrite.recipe:rewrite-static-analysis")

    annotationProcessor("org.openrewrite:rewrite-templating:latest.integration")
    compileOnly("com.google.errorprone:error_prone_core:2.19.1:with-dependencies") {
        exclude("com.google.auto.service", "auto-service-annotations")
    }

    testImplementation("org.openrewrite:rewrite-java-17")
    testImplementation("org.openrewrite:rewrite-test")
    testImplementation("org.openrewrite:rewrite-gradle")
    testImplementation("org.openrewrite:rewrite-maven")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:latest.release")
}
