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

package org.openrewrite.xml.liberty;

import org.junit.jupiter.api.Test;
import org.openrewrite.config.Environment;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.xml.Assertions.xml;

public class AppDDNamespaceTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(Environment.builder().scanRuntimeClasspath("org.openrewrite.java.liberty").build().activateRecipes("org.openrewrite.xml.liberty.AppDDNamespaceRule"));
    }

    @Test
    void replaceVersion14Test() {
        rewriteRun(
            xml(
                """
                    <application xmlns="http://java.sun.com/xml/ns/javaee"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/application_1_4.xsd"
                        version="1.4">
                        <description> DayTrader Stock Trading Performance Benchmark Sample </description>
                        <display-name>Trade</display-name>
                        <module>
                            <java>daytrader-streamer-1.0.jar</java>
                        </module>
                        <module>
                            <java>daytrader-wsappclient-1.0.jar</java>
                        </module>
                        <module>
                            <web>
                                <web-uri>daytrader-web-1.0.war</web-uri>
                                <context-root>/daytrader</context-root>
                            </web>
                        </module>
                        <module>
                            <ejb>daytrader-ejb-1.0.jar</ejb>
                        </module>
                    </application>
                """,
                """
                    <application xmlns="http://java.sun.com/xml/ns/j2ee"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/application_1_4.xsd"
                        version="1.4">
                        <description> DayTrader Stock Trading Performance Benchmark Sample </description>
                        <display-name>Trade</display-name>
                        <module>
                            <java>daytrader-streamer-1.0.jar</java>
                        </module>
                        <module>
                            <java>daytrader-wsappclient-1.0.jar</java>
                        </module>
                        <module>
                            <web>
                                <web-uri>daytrader-web-1.0.war</web-uri>
                                <context-root>/daytrader</context-root>
                            </web>
                        </module>
                        <module>
                            <ejb>daytrader-ejb-1.0.jar</ejb>
                        </module>
                    </application>
                """
            )
        );
    }

    @Test
    void replaceVersion5Test() {
        rewriteRun(
            xml(
                """
                    <application xmlns="http://java.sun.com/xml/ns/j2ee"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="5"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_5.xsd">

                        <module>
                            <java>test-client.jar</java>
                        </module>

                        <module>
                            <ejb>test-ejb.jar</ejb>
                        </module>

                        <module>
                            <web>
                                <web-uri>test.war</web-uri>
                                <context-root>test</context-root>
                            </web>
                        </module>

                        <library-directory>lib</library-directory>
                    </application>
                """,
                """
                    <application xmlns="http://java.sun.com/xml/ns/javaee"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="5"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_5.xsd">

                        <module>
                            <java>test-client.jar</java>
                        </module>

                        <module>
                            <ejb>test-ejb.jar</ejb>
                        </module>

                        <module>
                            <web>
                                <web-uri>test.war</web-uri>
                                <context-root>test</context-root>
                            </web>
                        </module>

                        <library-directory>lib</library-directory>
                    </application>
                """
            )
        );
    }

    @Test
    void replaceVersion6Test() {
        rewriteRun(
            xml(
                """
                    <application xmlns="http://java.sun.com/xml/ns/j2ee"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="6"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_6.xsd">

                        <module>
                            <java>test-client.jar</java>
                        </module>

                        <module>
                            <ejb>test-ejb.jar</ejb>
                        </module>

                        <module>
                            <web>
                                <web-uri>test.war</web-uri>
                                <context-root>test</context-root>
                            </web>
                        </module>

                        <library-directory>lib</library-directory>
                    </application>
                """,
                """
                    <application xmlns="http://java.sun.com/xml/ns/javaee"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="6"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_6.xsd">

                        <module>
                            <java>test-client.jar</java>
                        </module>

                        <module>
                            <ejb>test-ejb.jar</ejb>
                        </module>

                        <module>
                            <web>
                                <web-uri>test.war</web-uri>
                                <context-root>test</context-root>
                            </web>
                        </module>

                        <library-directory>lib</library-directory>
                    </application>
                """
            )
        );
    }

    @Test
    void replaceVersion7Test() {
        rewriteRun(
            xml(
                """
                    <application xmlns="http://java.sun.com/xml/ns/j2ee"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="7"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_7.xsd">

                        <application-name>test-app</application-name>
                        <initialize-in-order>true</initialize-in-order>
                        <module>
                            <web>
                                <web-uri>test-web.war</web-uri>
                                <context-root>test</context-root>
                            </web>
                        </module>

                        <module>
                            <ejb>test-ejb.jar</ejb>
                        </module>
                        <library-directory>lib</library-directory>
                    </application>
                """,
                """
                    <application xmlns="http://xmlns.jcp.org/xml/ns/javaee"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="7"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_7.xsd">

                        <application-name>test-app</application-name>
                        <initialize-in-order>true</initialize-in-order>
                        <module>
                            <web>
                                <web-uri>test-web.war</web-uri>
                                <context-root>test</context-root>
                            </web>
                        </module>

                        <module>
                            <ejb>test-ejb.jar</ejb>
                        </module>
                        <library-directory>lib</library-directory>
                    </application>
                """
            )
        );
    }

    @Test
    void replaceVersion8Test() {
        rewriteRun(
            xml(
                """
                    <application xmlns="http://java.sun.com/xml/ns/j2ee"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="8"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_8.xsd">

                        <application-name>test-app</application-name>
                        <initialize-in-order>true</initialize-in-order>
                        <module>
                            <web>
                                <web-uri>test-web.war</web-uri>
                                <context-root>test</context-root>
                            </web>
                        </module>

                        <module>
                            <ejb>test-ejb.jar</ejb>
                        </module>
                        <library-directory>lib</library-directory>
                    </application>
                """,
                """
                    <application xmlns="http://xmlns.jcp.org/xml/ns/javaee"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="8"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_8.xsd">

                        <application-name>test-app</application-name>
                        <initialize-in-order>true</initialize-in-order>
                        <module>
                            <web>
                                <web-uri>test-web.war</web-uri>
                                <context-root>test</context-root>
                            </web>
                        </module>

                        <module>
                            <ejb>test-ejb.jar</ejb>
                        </module>
                        <library-directory>lib</library-directory>
                    </application>
                """
            )
        );
    }
}

