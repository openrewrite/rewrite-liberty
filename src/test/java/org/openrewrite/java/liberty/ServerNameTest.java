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

class ServerNameTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new ServerName());
    }

    //language=java
    String serverNameClass = """
      package com.ibm.websphere.runtime;
      public class ServerName {
          public static String getDisplayName() {
              return "";
          }
          public static String getFullName() {
              return "";
          }
      }
      """;

    //language=java
    String adminServiceClass = """
      package com.ibm.websphere.management;
      public class AdminService {
          public static String getProcessName() {
              return "";
          }
      }
      """;

    //language=java
    String rasHelper = """
      package com.ibm.ejs.ras;
      public class RasHelper {
          public static String getServerName() {
              return "";
          }
      }
      """;

    @DocumentExample
    @Test
    void replaceGetFullNameTest() {
        rewriteRun(
          java(serverNameClass),
          //language=java
          java(
                """
              import com.ibm.websphere.runtime.ServerName;

              class ServerNameUsage {
                  void doX() {
                      ServerName.getFullName();
                  }
              }
              """,
                """
              class ServerNameUsage {
                  void doX() {
                      System.getProperty("wlp.server.name");
                  }
              }
              """
          )
        );
    }

    @Test
    void replaceGetDisplayNameTest() {
        rewriteRun(
          java(serverNameClass),
          //language=java
          java(
                """
              import com.ibm.websphere.runtime.ServerName;

              class ServerNameUsage {
                  void doX() {
                      ServerName.getDisplayName();
                  }
              }
              """,
                """
              class ServerNameUsage {
                  void doX() {
                      System.getProperty("wlp.server.name");
                  }
              }
              """
          )
        );
    }

    @Test
    void replaceGetProcessNameTest() {
        rewriteRun(
          java(adminServiceClass),
          //language=java
          java(
                """
              import com.ibm.websphere.management.AdminService;

              class ServerNameUsage {
                  void doX() {
                      AdminService.getProcessName();
                  }
              }
              """,
                """
              class ServerNameUsage {
                  void doX() {
                      System.getProperty("wlp.server.name");
                  }
              }
              """
          )
        );
    }

    @Test
    void replaceGetServerNameTest() {
        rewriteRun(
          java(rasHelper),
          //language=java
          java(
                """
              import com.ibm.ejs.ras.RasHelper;

              class ServerNameUsage {
                  void doX() {
                      RasHelper.getServerName();
                  }
              }
              """,
                """
              class ServerNameUsage {
                  void doX() {
                      System.getProperty("wlp.server.name");
                  }
              }
              """
          )
        );
    }
}
