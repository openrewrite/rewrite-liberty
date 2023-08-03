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
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public class InvalidInitialContextTest implements RewriteTest {
    
    String initialContextClass = """
        package javax.naming;

        public class InitialContext {

            public InitialContext() {
            }

            public void lookup() {
            }

        }
    """;

    @Test
    void replaceTimeoutTest() {
        rewriteRun(
            spec -> spec.recipe(new ChangeStringLiteral("^java:/comp(.*)$", "java:comp$1")),
            java(initialContextClass),
            java(
                """
                    package com.test;

                    import javax.naming.InitialContext;
                    import javax.naming.NamingException;

                    public class TestDetectInvalidInitialContext {
                        public static final String BAD_ENV = "java:/comp";
                        public static final String GOOD_ENV = "java:comp";
                        
                        private void doX(){
                            InitialContext ic = new InitialContext();
                            ic.lookup("java:/comp");
                            ic.lookup("java:/comp/BadEntry");
                            ic.lookup("java:comp/GoodEntry");
                        }
                    }
                """,
                """
                    package com.test;

                    import javax.naming.InitialContext;
                    import javax.naming.NamingException;

                    public class TestDetectInvalidInitialContext {
                        public static final String BAD_ENV = java:comp;
                        public static final String GOOD_ENV = "java:comp";

                        private void doX(){
                            InitialContext ic = new InitialContext();
                            ic.lookup(java:comp);
                            ic.lookup(java:comp/BadEntry);
                            ic.lookup("java:comp/GoodEntry");
                        }
                    }
                """
            )
        );
    }
    
}
