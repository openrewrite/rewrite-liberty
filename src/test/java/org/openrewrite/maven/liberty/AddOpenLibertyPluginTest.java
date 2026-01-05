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

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.mavenProject;
import static org.openrewrite.maven.Assertions.pomXml;

class AddOpenLibertyPluginTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipeFromResources("org.openrewrite.maven.liberty.AddOpenLibertyPlugin");
    }

    @DocumentExample
    @Test
    void addLibertyPlugin() {
        rewriteRun(
          //language=XML
          pomXml(
            """
              <project>
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
              <project>
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
        );
    }

    @Test
    void existingLibertyPlugin() {
        rewriteRun(
          //language=XML
          pomXml(
            """
              <project>
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
        );
    }

    @Test
    void multiModuleProject() {
        rewriteRun(
          mavenProject(
            "parent",
            //language=XML
            pomXml(
              """
                  <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.demo</groupId>
                    <artifactId>app-name</artifactId>
                    <version>1.0-SNAPSHOT</version>
                    <modules>
                        <module>child</module>
                    </modules>
                    <properties>
                        <maven.compiler.source>21</maven.compiler.source>
                        <maven.compiler.target>21</maven.compiler.target>
                        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                    </properties>
                </project>
                """,
              """
                  <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.demo</groupId>
                    <artifactId>app-name</artifactId>
                    <version>1.0-SNAPSHOT</version>
                    <modules>
                        <module>child</module>
                    </modules>
                    <properties>
                        <maven.compiler.source>21</maven.compiler.source>
                        <maven.compiler.target>21</maven.compiler.target>
                        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                    </properties>
                    <build>
                        <plugins>
                            <plugin>
                                <groupId>io.openliberty.tools</groupId>
                                <artifactId>liberty-maven-plugin</artifactId>
                                <version>3.10.3</version>
                            </plugin>
                        </plugins>
                    </build>
                </project>
                """
            ),
            mavenProject(
              "child",
              //language=XML
              pomXml(
                """
                  <project>
                      <modelVersion>4.0.0</modelVersion>
                      <parent>
                          <groupId>com.demo</groupId>
                          <artifactId>app-name</artifactId>
                          <version>1.0-SNAPSHOT</version>
                      </parent>
                      <artifactId>child-name</artifactId>
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
                  """
              )
            )
          )
        );
    }
}
