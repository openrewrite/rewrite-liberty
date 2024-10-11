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
package org.openrewrite.maven.liberty;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.*;
import static org.openrewrite.maven.Assertions.pomXml;

class AddOpenLibertyPluginDependencyTest implements RewriteTest {
    @Language("java")
    private final String sampleClass = """
         package com.test;

         public class Test {         
              public static void main(String[] args)
              {
                  System.out.println("test");
               }
         }     
      """;

    @Override
    public void defaults(RecipeSpec spec) {
        spec.parser(JavaParser.fromJavaVersion());
    }

    @Test
    void testAddLibertyPlugin() {
        rewriteRun(
          spec -> spec.recipeFromResources("org.openrewrite.maven.liberty.AddOpenLibertyPluginDependency"),
          mavenProject(
            "project",
            srcMainJava(
              java(sampleClass)
            ),
            pomXml(
              """
                    <project>
                        <groupId>com.mycompany.app</groupId>
                        <artifactId>my-app</artifactId>
                        <version>1</version>
                    </project>
                """,
              """
                    <project>
                        <groupId>com.mycompany.app</groupId>
                        <artifactId>my-app</artifactId>
                        <version>1</version>
                        <dependencies>
                            <dependency>
                                <groupId>io.openliberty.tools</groupId>
                                <artifactId>liberty-maven-plugin</artifactId>
                                <version>3.10.3</version>
                            </dependency>
                        </dependencies>
                    </project>
                """
            )
          )
        );
    }
    @Test
    void testExistingLibertyPlugin() {
        rewriteRun(
          spec -> spec.recipeFromResources("org.openrewrite.maven.liberty.AddOpenLibertyPluginDependency"),
          mavenProject(
            "project",
            srcMainJava(
              java(sampleClass)
            ),
            pomXml(
              """
                     <project>
                        <groupId>com.mycompany.app</groupId>
                        <artifactId>my-app</artifactId>
                        <version>1</version>
                        <dependencies>
                            <dependency>
                                <groupId>io.openliberty.tools</groupId>
                                <artifactId>liberty-maven-plugin</artifactId>
                                <version>3.10.3</version>
                            </dependency>
                        </dependencies>
                    </project>
                """
            )
          )
        );
    }
}
