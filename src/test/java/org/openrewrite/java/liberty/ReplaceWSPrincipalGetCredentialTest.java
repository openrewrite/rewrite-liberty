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
