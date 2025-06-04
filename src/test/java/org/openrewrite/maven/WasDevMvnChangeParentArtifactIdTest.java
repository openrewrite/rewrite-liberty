/*
 * Copyright 2024 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.maven;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.mavenProject;
import static org.openrewrite.maven.Assertions.pomXml;

class WasDevMvnChangeParentArtifactIdTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.parser(JavaParser.fromJavaVersion());
    }

    @DocumentExample
    @Test
    void mvnChangeParentArtifactId() {
        rewriteRun(
          spec -> spec.recipeFromResources("org.openrewrite.maven.WasDevMvnChangeParentArtifactId"),
          //language=XML
          pomXml(
            """
              <project xmlns="http://maven.apache.org/POM/4.0.0"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                                           http://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                   <parent>
                        <groupId>net.wasdev.maven.parent</groupId>
                         <artifactId>java8-parent</artifactId>
                         <version>1.4</version>
                   </parent>
                   <artifactId>my-artifact</artifactId>
              </project>
              """,
            """
              <project xmlns="http://maven.apache.org/POM/4.0.0"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                                           http://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                   <parent>
                        <groupId>net.wasdev.maven.parent</groupId>
                         <artifactId>parent</artifactId>
                         <version>1.4</version>
                   </parent>
                   <artifactId>my-artifact</artifactId>
              </project>
              """
          )
        );
    }

    @Test
    void noChangeParentArtifactId() {
        rewriteRun(
          spec -> spec.recipeFromResources("org.openrewrite.maven.WasDevMvnChangeParentArtifactId"),
          //language=XML
          pomXml(
            """
              <project xmlns="http://maven.apache.org/POM/4.0.0"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                                           http://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                   <parent>
                        <groupId>net.wasdev.maven.parent</groupId>
                         <artifactId>parent</artifactId>
                         <version>1.4</version>
                   </parent>
                   <artifactId>my-artifact</artifactId>
              </project>
              """
          )
        );
    }
}
