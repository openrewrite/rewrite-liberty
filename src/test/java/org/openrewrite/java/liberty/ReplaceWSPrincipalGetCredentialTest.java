package org.openrewrite.java.liberty;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.TypeValidation;

import static org.openrewrite.java.Assertions.java;

class ReplaceWSPrincipalGetCredentialTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new ReplaceWSPrincipalGetCredential())
          // disable all type checks so our stubs + injected code don’t produce LST errors
          .typeValidationOptions(TypeValidation.none());
    }

    @DocumentExample
    @Test
    void assignmentRewrite() {
        rewriteRun(
          // stub WSPrincipal
          java(
            """
            package com.ibm.websphere.security.auth;
            import com.ibm.websphere.security.cred.WSCredential;
            public class WSPrincipal {
                public static WSCredential getCredential() { return null; }
            }
            """
          ),
          // stub WSSubject
          java(
            """
            package com.ibm.websphere.security.auth;
            import javax.security.auth.Subject;
            public class WSSubject {
                public static Subject getCallerSubject() { return null; }
            }
            """
          ),
          // stub WSCredential
          java(
            """
            package com.ibm.websphere.security.cred;
            public class WSCredential {}
            """
          ),
          // before → after
          java(
            // before
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
            // after
            """
            package com.acme;

            import com.ibm.websphere.security.auth.WSSubject;
            import com.ibm.websphere.security.cred.WSCredential;

            class A {
                void fetchCredential() {
                    WSCredential credential = null;
                    try {
                        javax.security.auth.Subject subject = WSSubject.getCallerSubject();
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
