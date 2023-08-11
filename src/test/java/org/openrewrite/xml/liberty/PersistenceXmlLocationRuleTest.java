/*
 * Copyright 2023 the original author or authors.
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

package org.openrewrite.xml.liberty;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.config.Environment;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openrewrite.java.Assertions.project;
import static org.openrewrite.test.SourceSpecs.text;

class PersistenceXmlLocationRuleTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(
          Environment.builder()
            .scanRuntimeClasspath("org.openrewrite.java.liberty")
            .build()
            .activateRecipes("org.openrewrite.xml.liberty.PersistenceXmlLocationRule"));
    }

    @DocumentExample
    @Test
    void movePersistenceXMLFileTest() {
        rewriteRun(
          text(
            //language=xml
            """
              <persistence version="2.0" xmlns="http://java.sun.com/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd">
                <persistence-unit name="ejb">
                </persistence-unit>
              </persistence>
              """,
            spec -> project(spec, "testEjbWithJpa").path("testEjbWithJpa/notsrc/META-INF/persistence.xml")
              .afterRecipe(pt -> assertThat(pt.getSourcePath()).isEqualTo(Paths.get(System.getProperty("user.dir"), "testEjbWithJpa/src/META-INF/persistence.xml")))
          )
        );
    }
}
