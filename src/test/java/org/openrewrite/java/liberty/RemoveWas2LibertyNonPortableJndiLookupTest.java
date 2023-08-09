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

public class RemoveWas2LibertyNonPortableJndiLookupTest implements RewriteTest {

    @Test
    void removeInvalidPropertiesTest() {
        rewriteRun(
                spec -> spec.recipe(Environment.builder().scanRuntimeClasspath("org.openrewrite.java.liberty").build().activateRecipes("org.openrewrite.java.liberty.RemoveWas2LibertyNonPortableJndiLookup")),
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

                                            InitialContext ctx = new InitialContext(ht);
                                        }

                                    }
                                """
                )
        );
    }

}
