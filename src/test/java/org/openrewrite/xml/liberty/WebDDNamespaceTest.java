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
import org.openrewrite.config.Environment;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.xml.Assertions.xml;

class WebDDNamespaceTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(
          Environment.builder()
            .scanRuntimeClasspath("org.openrewrite.java.liberty")
            .build()
            .activateRecipes("org.openrewrite.xml.liberty.WebDDNamespaceRule"));
    }

    @Test
    void replaceVersion24Test() {
        rewriteRun(
          //language=xml
          xml(
            """
              <web-app xmlns="http://java.sun.com/xml/ns/javaee" version="2.4" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd" id="WebApp_ID">
                  <display-name>testWebDDNamespace</display-name>
              </web-app>
              """,
            """
              <web-app xmlns="http://java.sun.com/xml/ns/j2ee" version="2.4" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd" id="WebApp_ID">
                  <display-name>testWebDDNamespace</display-name>
              </web-app>
              """
          )
        );
    }

    @Test
    void replaceVersion25Test() {
        rewriteRun(
          //language=xml
          xml(
            """
              <web-app xmlns="http://java.sun.com/xml/ns/j2ee" version="2.5" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" id="WebApp_ID">
                  <display-name>testWebDDNamespace</display-name>
              </web-app>
              """,
            """
              <web-app xmlns="http://java.sun.com/xml/ns/java" version="2.5" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" id="WebApp_ID">
                  <display-name>testWebDDNamespace</display-name>
              </web-app>
              """
          )
        );
    }

    @Test
    void replaceVersion30Test() {
        //language=xml
        rewriteRun(
          //language=xml
          xml(
            """
              <web-app xmlns="http://java.sun.com/xml/ns/j2ee" version="3.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_3_0.xsd" id="WebApp_ID">
                  <display-name>testWebDDNamespace</display-name>
              </web-app>
              """,
            """
              <web-app xmlns="http://java.sun.com/xml/ns/java" version="3.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_3_0.xsd" id="WebApp_ID">
                  <display-name>testWebDDNamespace</display-name>
              </web-app>
              """
          )
        );
    }

    @Test
    void replaceVersion31Test() {
        rewriteRun(
          //language=xml
          xml(
            """
              <web-app xmlns="http://java.sun.com/xml/ns/j2ee" version="3.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_3_1.xsd" id="WebApp_ID">
                  <display-name>testWebDDNamespace</display-name>
              </web-app>
              """,
            """
              <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" version="3.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_3_1.xsd" id="WebApp_ID">
                  <display-name>testWebDDNamespace</display-name>
              </web-app>
              """
          )
        );
    }

    @Test
    void replaceVersion32Test() {
        rewriteRun(
          //language=xml
          xml(
            """
               <web-app xmlns="http://java.sun.com/xml/ns/j2ee" version="3.2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="WebApp_ID">
                   <display-name>testWebDDNamespace</display-name>
               </web-app>
              """,
            """
              <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" version="3.2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="WebApp_ID">
                  <display-name>testWebDDNamespace</display-name>
              </web-app>
              """
          )
        );
    }
}

