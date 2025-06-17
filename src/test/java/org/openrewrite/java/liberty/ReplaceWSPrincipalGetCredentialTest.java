/*
 * Copyright 2025 the original author or authors.
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
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class ReplaceWSPrincipalGetCredentialTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec
          .recipe(new ReplaceWSPrincipalGetCredential())
          //language=java
          .parser(JavaParser.fromJavaVersion()
            .dependsOn(
              """
                package com.ibm.websphere.security.auth;
                import com.ibm.websphere.security.cred.WSCredential;
                public class WSPrincipal {
                    public static WSCredential getCredential() { return null; }
                }
                """,
              """
                package com.ibm.websphere.security.auth;
                import javax.security.auth.Subject;
                public class WSSubject {
                    public static Subject getCallerSubject() { return null; }
                }
                """,
              """
                package com.ibm.websphere.security.cred;
                public class WSCredential {}
                """
            )
          );
    }

    @DocumentExample
    @Test
    void assignmentRewrite() {
        rewriteRun(
          //language=java
          java(
            """
              package com.acme;

              import com.ibm.websphere.security.auth.WSPrincipal;
              import com.ibm.websphere.security.cred.WSCredential;

              class A {
                  void fetchCredential() {
                      WSCredential credential = WSPrincipal.getCredential();
                  }
              }
              """,
            """
              package com.acme;

              import com.ibm.websphere.security.auth.WSSubject;
              import com.ibm.websphere.security.cred.WSCredential;

              import javax.security.auth.Subject;

              class A {
                  void fetchCredential() {
                      WSCredential credential = null;
                      try {
                          Subject subject = WSSubject.getCallerSubject();
                          if (subject != null) {
                              credential = subject.getPublicCredentials(WSCredential.class)
                                      .iterator().next();
                          }
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                  }
              }
              """
          )
        );
    }
}
