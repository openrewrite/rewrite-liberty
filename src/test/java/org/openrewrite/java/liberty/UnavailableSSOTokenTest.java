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
