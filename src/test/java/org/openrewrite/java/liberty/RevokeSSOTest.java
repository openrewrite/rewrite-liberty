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