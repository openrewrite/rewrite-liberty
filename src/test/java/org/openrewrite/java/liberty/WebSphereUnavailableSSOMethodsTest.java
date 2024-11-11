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
package org.openrewrite.java.liberty;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.config.Environment;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class WebSphereUnavailableSSOMethodsTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.parser(JavaParser.fromJavaVersion().classpathFromResources(new InMemoryExecutionContext(), "websecurity_logout_test", "websecurity_logout_test"))
          .recipe(Environment.builder()
            .scanRuntimeClasspath("org.openrewrite.java.liberty")
            .build().activateRecipes("org.openrewrite.java.liberty.WebSphereUnavailableSSOMethods"));
    }

    @DocumentExample
    @Test
    void updatesLPTACookieTokenSSO() {
        rewriteRun(
          //language=java
          java(
            """
              import com.ibm.websphere.security.WSSecurityHelper;

              import javax.servlet.http.Cookie;
              import javax.servlet.http.HttpServletRequest;
              import javax.servlet.http.HttpServletResponse;

              public class Test {
                  public void doX() {
                      Cookie ltpaCookie = WSSecurityHelper.getLTPACookieFromSSOToken();
                  }
                  void revoke(HttpServletRequest req, HttpServletResponse res) {
                      WSSecurityHelper.revokeSSOCookies(req,res);
                  }
              }
              """, """
              import com.ibm.websphere.security.web.WebSecurityHelper;

              import javax.servlet.http.Cookie;
              import javax.servlet.http.HttpServletRequest;
              import javax.servlet.http.HttpServletResponse;

              public class Test {
                  public void doX() {
                      Cookie ltpaCookie = WebSecurityHelper.getSSOCookieFromSSOToken();
                  }
                  void revoke(HttpServletRequest req, HttpServletResponse res) {
                      req.logout();
                  }
              }
              """
          )
        );
    }

}
