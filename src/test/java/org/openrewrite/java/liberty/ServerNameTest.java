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

    @Test
    void replaceGetFullNameTest() {
        rewriteRun(
          java(serverNameClass),
          //language=java
          java(
            """
              package com.ibm;

              import com.ibm.websphere.runtime.ServerName;

              public class ServerNameUsage {
                  
                  public void doX() {
                      ServerName.getFullName();
                  }

              }
              """,
            """
              package com.ibm;

              public class ServerNameUsage {
                  
                  public void doX() {
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
              package com.ibm;

              import com.ibm.websphere.runtime.ServerName;

              public class ServerNameUsage {
                  
                  public void doX() {
                      ServerName.getDisplayName();
                  }

              }
              """,
            """
              package com.ibm;

              public class ServerNameUsage {
                  
                  public void doX() {
                      System.getProperty("wlp.server.name");
                  }

              }
              """
          )
        );
    }
}
