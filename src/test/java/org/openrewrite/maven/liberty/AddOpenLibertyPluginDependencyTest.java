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
