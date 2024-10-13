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
    void addLibertyPlugin() {
        rewriteRun(
          spec -> spec.recipeFromResources("org.openrewrite.maven.liberty.AddOpenLibertyPluginDependency"),
          mavenProject(
            "project",
            srcMainJava(
              java(sampleClass)
            ),
            pomXml(
              """
                <?xml version="1.0" encoding="UTF-8" ?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.demo</groupId>
                    <artifactId>app-name</artifactId>
                    <version>1.0-SNAPSHOT</version>
                    <packaging>war</packaging>
                    <properties>
                        <maven.compiler.source>21</maven.compiler.source>
                        <maven.compiler.target>21</maven.compiler.target>
                        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                    </properties>
                    <dependencies>
                        <dependency>
                            <groupId>org.eclipse.microprofile</groupId>
                            <artifactId>microprofile</artifactId>
                            <version>6.1</version>
                            <type>pom</type>
                            <scope>provided</scope>
                        </dependency>
                    </dependencies>
                    <build>
                        <plugins>
                            <plugin>
                                <groupId>org.apache.maven.plugins</groupId>
                                <artifactId>maven-war-plugin</artifactId>
                                <version>3.3.2</version>
                            </plugin>
                        </plugins>
                    </build>
                </project>
                """,
              """
                <?xml version="1.0" encoding="UTF-8" ?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.demo</groupId>
                    <artifactId>app-name</artifactId>
                    <version>1.0-SNAPSHOT</version>
                    <packaging>war</packaging>
                    <properties>
                        <maven.compiler.source>21</maven.compiler.source>
                        <maven.compiler.target>21</maven.compiler.target>
                        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                    </properties>
                    <dependencies>
                        <dependency>
                            <groupId>org.eclipse.microprofile</groupId>
                            <artifactId>microprofile</artifactId>
                            <version>6.1</version>
                            <type>pom</type>
                            <scope>provided</scope>
                        </dependency>
                    </dependencies>
                    <build>
                        <plugins>
                            <plugin>
                                <groupId>org.apache.maven.plugins</groupId>
                                <artifactId>maven-war-plugin</artifactId>
                                <version>3.3.2</version>
                            </plugin>
                            <plugin>
                                <groupId>io.openliberty.tools</groupId>
                                <artifactId>liberty-maven-plugin</artifactId>
                                <version>3.10.3</version>
                            </plugin>
                        </plugins>
                    </build>
                </project>
                """
            )
          )
        );
    }
    @Test
    void existingLibertyPlugin() {
        rewriteRun(
          spec -> spec.recipeFromResources("org.openrewrite.maven.liberty.AddOpenLibertyPluginDependency"),
          mavenProject(
            "project",
            srcMainJava(
              java(sampleClass)
            ),
            pomXml(
                   """
                   <?xml version="1.0" encoding="UTF-8" ?>
                   <project xmlns="http://maven.apache.org/POM/4.0.0"
                             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                        <modelVersion>4.0.0</modelVersion>
                        <groupId>com.demo</groupId>
                        <artifactId>app-name</artifactId>
                        <version>1.0-SNAPSHOT</version>
                        <packaging>war</packaging>
                        <properties>
                            <maven.compiler.source>21</maven.compiler.source>
                            <maven.compiler.target>21</maven.compiler.target>
                            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                        </properties>
                        <dependencies>
                            <dependency>
                                <groupId>org.eclipse.microprofile</groupId>
                                <artifactId>microprofile</artifactId>
                                <version>6.1</version>
                                <type>pom</type>
                                <scope>provided</scope>
                            </dependency>
                        </dependencies>
                        <build>
                            <plugins>
                                <plugin>
                                    <groupId>org.apache.maven.plugins</groupId>
                                    <artifactId>maven-war-plugin</artifactId>
                                    <version>3.3.2</version>
                                </plugin>
                                <plugin>
                                    <groupId>io.openliberty.tools</groupId>
                                    <artifactId>liberty-maven-plugin</artifactId>
                                    <version>3.10.3</version>
                                </plugin>
                            </plugins>
                        </build>
                   </project>
                   """
            )
          )
        );
    }
}
