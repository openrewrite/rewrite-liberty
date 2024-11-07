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

package org.openrewrite.java.liberty;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class RemoveWas2LibertyNonPortableJndiLookupTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RemoveWas2LibertyNonPortableJndiLookup());
    }


    @DocumentExample
    @Test
    void literalTest() {
        rewriteRun(
          //language=java
          java(
            """
              package com.ibm;

              import java.util.Hashtable;
              import javax.naming.InitialContext;

              public class ServerNameUsage {

                  public void doX() {
                      Hashtable ht = new Hashtable();
                      ht.put("java.naming.factory.initial", "com.ibm.websphere.naming.WsnInitialContextFactory");
                      ht.put("java.naming.provider.url", "corbaloc:iiop:localhost:2809");
                      ht.put("valid", "valid");

                      InitialContext ctx = new InitialContext(ht);
                  }

              }
              """,
            """
              package com.ibm;

              import java.util.Hashtable;
              import javax.naming.InitialContext;

              public class ServerNameUsage {

                  public void doX() {
                      Hashtable ht = new Hashtable();
                      ht.put("valid", "valid");

                      InitialContext ctx = new InitialContext(ht);
                  }

              }
              """
          )
        );
    }

    @Test
    void variableTest() {
        rewriteRun(
          //language=java
          java(
            """
              package com.ibm;

              import java.util.Hashtable;
              import javax.naming.InitialContext;

              public class ServerNameUsage {

                  public void doX() {
                      Hashtable<String, String> env = new Hashtable<String, String>();
                      String initial = "java.naming.factory.initial";
                      String url = "java.naming.provider.url";
                      env.put(initial, "com.ibm.websphere.naming.WsnInitialContextFactory");
                      env.put(url, "corbaloc:iiop:localhost:2809");
                      env.put("valid", "valid");

                      InitialContext ctx = new InitialContext(env);
                  }

              }
              """,
            """
              package com.ibm;

              import java.util.Hashtable;
              import javax.naming.InitialContext;

              public class ServerNameUsage {

                  public void doX() {
                      Hashtable<String, String> env = new Hashtable<String, String>();
                      env.put("valid", "valid");

                      InitialContext ctx = new InitialContext(env);
                  }

              }
              """
          )
        );
    }

    @Test
    void variableAssignmentTest() {
        rewriteRun(
          //language=java
          java(
            """
              package com.ibm;

              import java.util.Hashtable;
              import javax.naming.InitialContext;

              public class ServerNameUsage {

                  public void doX() {
                      Hashtable<String, String> env = new Hashtable<String, String>();
                      String initial = "";
                      String url = "";
                      initial = "java.naming.factory.initial";
                      url = "java.naming.provider.url";
                      env.put(initial, "com.ibm.websphere.naming.WsnInitialContextFactory");
                      env.put(url, "corbaloc:iiop:localhost:2809");
                      env.put("valid", "valid");

                      InitialContext ctx = new InitialContext(env);
                  }

              }
              """,
            """
              package com.ibm;

              import java.util.Hashtable;
              import javax.naming.InitialContext;

              public class ServerNameUsage {

                  public void doX() {
                      Hashtable<String, String> env = new Hashtable<String, String>();
                      env.put("valid", "valid");

                      InitialContext ctx = new InitialContext(env);
                  }

              }
              """
          )
        );
    }

    @Test
    void variableUnrelatedReassignmentTest() {
        rewriteRun(
          //language=java
          java(
            """
              package com.ibm;

              import java.util.Hashtable;
              import javax.naming.InitialContext;

              public class ServerNameUsage {

                  public void doX() {
                      Hashtable<String, String> env = new Hashtable<String, String>();
                      String initial = "java.naming.factory.initial";
                      String url = "java.naming.provider.url";
                      initial = "valid";
                      url = "valid";
                      env.put(initial, "com.ibm.websphere.naming.WsnInitialContextFactory");
                      env.put(url, "corbaloc:iiop:localhost:2809");
                      env.put("valid", "valid");

                      InitialContext ctx = new InitialContext(env);
                  }

              }
              """
          )
        );
    }

    @Test
    void fieldTest() {
        rewriteRun(
          //language=java
          java(
            """
              package com.ibm;

              import java.util.Hashtable;
              import javax.naming.InitialContext;

              public class ServerNameUsage {
                  private String initial = "java.naming.factory.initial";
                  private String url = "java.naming.provider.url";

                  public void doX() {
                      Hashtable<String, String> env = new Hashtable<String, String>();
                      env.put(initial, "com.ibm.websphere.naming.WsnInitialContextFactory");
                      env.put(url, "corbaloc:iiop:localhost:2809");
                      env.put("valid", "valid");

                      InitialContext ctx = new InitialContext(env);
                  }

              }
              """,
            """
              package com.ibm;

              import java.util.Hashtable;
              import javax.naming.InitialContext;

              public class ServerNameUsage {

                  public void doX() {
                      Hashtable<String, String> env = new Hashtable<String, String>();
                      env.put("valid", "valid");

                      InitialContext ctx = new InitialContext(env);
                  }

              }
              """
          )
        );
    }

    @Test
    void fieldReassignmentTest() {
        rewriteRun(
          //language=java
          java(
            """
              package com.ibm;

              import java.util.Hashtable;
              import javax.naming.InitialContext;

              public class ServerNameUsage {
                  private String initial = "";
                  private String url = "";

                  public void doX() {
                      Hashtable<String, String> env = new Hashtable<String, String>();
                      initial = "java.naming.factory.initial";
                      url = "java.naming.provider.url";
                      env.put(initial, "com.ibm.websphere.naming.WsnInitialContextFactory");
                      env.put(url, "corbaloc:iiop:localhost:2809");
                      env.put("valid", "valid");

                      InitialContext ctx = new InitialContext(env);
                  }

              }
              """,
            """
              package com.ibm;

              import java.util.Hashtable;
              import javax.naming.InitialContext;

              public class ServerNameUsage {
                  private String initial = "";
                  private String url = "";

                  public void doX() {
                      Hashtable<String, String> env = new Hashtable<String, String>();
                      initial = "java.naming.factory.initial";
                      url = "java.naming.provider.url";
                      env.put("valid", "valid");

                      InitialContext ctx = new InitialContext(env);
                  }

              }
              """
          )
        );
    }

}
