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
import org.openrewrite.config.Environment;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public class RevokeSSOTest implements RewriteTest {
    String wsSecurityHelperClass = """
             package com.ibm.websphere.security;
             
             public class WSSecurityHelper {
             
                public static  void revokeSSOCookies(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse res){
                  
                }
                
                     
                 }         
                       
            """;
    String httpServletRequestClass = """
                     package javax.servlet.http;

                     public class HttpServletRequest {
                         
                         public static void logout() {
                             
                         }
                         
                     }
            """;
    String httpServletResponseClass = """
                 package javax.servlet.http;

                 public class HttpServletResponse {
                 
                 }
                    
            """;

    @Test
    void doesNotModifyLogoutAlreadyPresent() {
        rewriteRun(
                // java(httpServletRequestClass),
                java(
                        """
                                    package com.ibm;
                                    import javax.servlet.http.HttpServletRequest;
                                  
                                    public class Test{
                                            public void doX() {
                                                HttpServletRequest res;
                                                res.logout();
                                             }
                                    }
                                """
                )
        );
    }

    @Test
    void doesNotModifyMethodNotPresent() {
        rewriteRun(
                java(wsSecurityHelperClass),
                java(
                        """
                                    package com.ibm;
                                    com.ibm.websphere.security.web.WebSecurityHelper; 
                                    
                                    public class Test{               

                                       int x = 10;
                                       public void  helloworld(){
                                            System.out.println("hello world");
                                       }
                                    }
                                """
                )
        );
    }

    @Test
    void revokeSSOAddLogout() {
        rewriteRun(
                spec -> spec.recipe(Environment.builder().scanRuntimeClasspath("org.openrewrite.java.liberty").build().activateRecipes("org.openrewrite.java.liberty.WebSphereUnavailableSSOCookieMethod")),
                java(httpServletRequestClass),
                java(httpServletResponseClass),
                java(wsSecurityHelperClass),
                java(
                        """
                                    package com.ibm;
                                    import com.ibm.websphere.security.WSSecurityHelper;
                                    public class Test{   
                                        public void revoke() {
                                            javax.servlet.http.HttpServletRequest req = null;
                                            javax.servlet.http.HttpServletResponse res = null;
                                            WSSecurityHelper.revokeSSOCookies(req,res);
                                        }
                                    }
                                """,
                        """
                                    package com.ibm;
                                    public class Test{
                                        public void revoke() {
                                            javax.servlet.http.HttpServletRequest req = null;
                                            javax.servlet.http.HttpServletResponse res = null;
                                            req.logout();
                                        }
                                    }
                                """
                )
        );
    }
}