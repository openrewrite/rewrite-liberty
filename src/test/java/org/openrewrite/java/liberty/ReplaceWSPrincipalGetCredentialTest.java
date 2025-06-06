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

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.TypeValidation;

import static org.openrewrite.java.Assertions.java;

public class ReplaceWSPrincipalGetCredentialTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec
          .parser(JavaParser.fromJavaVersion())
          .recipe(new ReplaceWSPrincipalGetCredential());
    }

    @Test
    void replaceGetCredentialBodyWithoutStubs() {
        // BEFORE: a legacy getCredential() returning a helper’s result
        @Language("java")
        String before = """
            package com.example;

            import com.ibm.websphere.security.auth.WSPrincipal;
            import com.ibm.websphere.security.cred.WSCredential;

            public class MyPrincipal implements WSPrincipal {
                @Override
                public WSCredential getCredential() {
                    return fetchLegacyCredential();
                }

                private WSCredential fetchLegacyCredential() {
                    return null;
                }
            }
            """;

        //  AFTER: same signature, but body replaced with WSSubject.getCallerSubject()
        @Language("java")
        String after = """
            package com.example;

            import com.ibm.websphere.security.auth.WSPrincipal;
            import com.ibm.websphere.security.cred.WSCredential;

            public class MyPrincipal implements WSPrincipal {
                @Override
                public WSCredential getCredential() {
                    com.ibm.websphere.security.cred.WSCredential credential = null;
                    try {
                        javax.security.auth.Subject subject =
                            com.ibm.websphere.security.auth.WSSubject.getCallerSubject();
                        if (subject != null) {
                            credential = subject.getPublicCredentials(
                                com.ibm.websphere.security.cred.WSCredential.class
                            ).iterator().next();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return credential;
                }

                private WSCredential fetchLegacyCredential() {
                    return null;
                }
            }
            """;

        rewriteRun(
          spec -> spec
            .typeValidationOptions(TypeValidation.none())
            .expectedCyclesThatMakeChanges(2),
          java(before, after)
        );
    }
}
