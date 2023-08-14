package org.openrewrite.java.liberty;

import org.junit.jupiter.api.Test;
import org.openrewrite.config.Environment;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public class UnavailableSSOTokenTest implements RewriteTest {

    String ssoTokenNameClass = """
                                package com.ibm.websphere.security;

                                import javax.servlet.http.Cookie;
                                
                                public class WSSecurityHelper {
                                
                                
                                        public static  Cookie getLTPACookieFromSSOToken(){
                                            return new Cookie();
                                            
                                        }
                                    }         
                                          
                               """;
    String cookieNameClass = """
                            package javax.servlet.http;

                            public class Cookie {

                            }  
                            """;
    
    
                            
    
    @Test
    void doesNotModifySSOtokensAlreadyPresent() {
        rewriteRun(
            java(cookieNameClass),
            java(ssoTokenNameClass),
            java(
                """
                    package com.ibm;
                    import com.ibm.websphere.security.WSSecurityHelper;
                    com.ibm.websphere.security.web.WebSecurityHelper; 
                    public class Test{
                            public void doX() {
                                Cookie ltpaCookie = WebSecurityHelper.getSSOCookieFromSSOToken();
                             }
                    }
                """
            )
        );
    }
    @Test
    void doesNotModifySSOtokensNotPresent() {
        rewriteRun(
            java(cookieNameClass),
            java(ssoTokenNameClass),
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
    void updatesLPTACookieToSSO() {
        rewriteRun(
            spec -> spec.recipe(Environment.builder().scanRuntimeClasspath("org.openrewrite.java.liberty").build().activateRecipes("org.openrewrite.java.liberty.WebSphereUnavailableSSOTokenMethod")),      
            java(cookieNameClass),
            java(ssoTokenNameClass),
            java(
                """
                    package com.ibm;
                    
                    import javax.servlet.http.Cookie;
                    import com.ibm.websphere.security.WSSecurityHelper;

                    public class Test{               

                        public void doX() {
                            Cookie ltpaCookie = WSSecurityHelper.getLTPACookieFromSSOToken();
                        }
                    }                   
                """,
                """
                    package com.ibm;

                    import com.ibm.websphere.security.web.WSSecurityHelper;
                    
                    import javax.servlet.http.Cookie;
                    
                    public class Test{
                    
                        public void doX() {
                            Cookie ltpaCookie = WSSecurityHelper.getSSOCookieFromSSOToken();
                        }
                    }
                """
            )
        );
    } 
    
}
