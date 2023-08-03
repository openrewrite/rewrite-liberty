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

public class ConnectorDDNamespaceTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(Environment.builder().scanRuntimeClasspath("org.openrewrite.java.liberty").build().activateRecipes("org.openrewrite.xml.liberty.ConnectorDDNamespaceRule"));
    }

    @Test
    void replaceVersion15Test() {
        rewriteRun(
            xml(
                """
                    <connector xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
                            http://java.sun.com/xml/ns/j2ee/connector_1_5.xsd"
                        version="1.5">
                        <display-name>Generic JCA</display-name>
                        <vendor-name>adam-bien.com</vendor-name>
                        <eis-type>Generic JCA</eis-type>
                        <resourceadapter-version>1.0</resourceadapter-version>
                        <resourceadapter>
                            <outbound-resourceadapter>
                                <connection-definition>
                                    <managedconnectionfactory-class>...genericjca.GenericManagedConnectionFactory</managedconnectionfactory-class>
                                    <connectionfactory-interface>...genericjca.DataSource</connectionfactory-interface>
                                    <connectionfactory-impl-class>...genericjca.FileDataSource</connectionfactory-impl-class>
                                    <connection-interface>...genericjca.Connection</connection-interface>
                                    <connection-impl-class>...genericjca.FileConnection</connection-impl-class>
                                </connection-definition>
                                <transaction-support>LocalTransaction</transaction-support>
                                <authentication-mechanism>
                                    <authentication-mechanism-type>BasicPassword</authentication-mechanism-type>
                                    <credential-interface>javax.resource.spi.security.PasswordCredential</credential-interface>
                                </authentication-mechanism>
                                <reauthentication-support>false</reauthentication-support>
                            </outbound-resourceadapter>
                        </resourceadapter>
                    </connector>
                """,
                """
                    <connector xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
                            http://java.sun.com/xml/ns/j2ee/connector_1_5.xsd"
                        version="1.5">
                        <display-name>Generic JCA</display-name>
                        <vendor-name>adam-bien.com</vendor-name>
                        <eis-type>Generic JCA</eis-type>
                        <resourceadapter-version>1.0</resourceadapter-version>
                        <resourceadapter>
                            <outbound-resourceadapter>
                                <connection-definition>
                                    <managedconnectionfactory-class>...genericjca.GenericManagedConnectionFactory</managedconnectionfactory-class>
                                    <connectionfactory-interface>...genericjca.DataSource</connectionfactory-interface>
                                    <connectionfactory-impl-class>...genericjca.FileDataSource</connectionfactory-impl-class>
                                    <connection-interface>...genericjca.Connection</connection-interface>
                                    <connection-impl-class>...genericjca.FileConnection</connection-impl-class>
                                </connection-definition>
                                <transaction-support>LocalTransaction</transaction-support>
                                <authentication-mechanism>
                                    <authentication-mechanism-type>BasicPassword</authentication-mechanism-type>
                                    <credential-interface>javax.resource.spi.security.PasswordCredential</credential-interface>
                                </authentication-mechanism>
                                <reauthentication-support>false</reauthentication-support>
                            </outbound-resourceadapter>
                        </resourceadapter>
                    </connector>
                """
            )
        );
    }

    @Test
    void replaceVersion16Test() {
        rewriteRun(
            xml(
                """
                    <connector xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
                            http://java.sun.com/xml/ns/j2ee/connector_1_6.xsd"
                        version="1.6">
                        <display-name>Generic JCA</display-name>
                        <vendor-name>adam-bien.com</vendor-name>
                        <eis-type>Generic JCA</eis-type>
                        <resourceadapter-version>1.0</resourceadapter-version>
                        <resourceadapter>
                            <outbound-resourceadapter>
                                <connection-definition>
                                    <managedconnectionfactory-class>...genericjca.GenericManagedConnectionFactory</managedconnectionfactory-class>
                                    <connectionfactory-interface>...genericjca.DataSource</connectionfactory-interface>
                                    <connectionfactory-impl-class>...genericjca.FileDataSource</connectionfactory-impl-class>
                                    <connection-interface>...genericjca.Connection</connection-interface>
                                    <connection-impl-class>...genericjca.FileConnection</connection-impl-class>
                                </connection-definition>
                                <transaction-support>LocalTransaction</transaction-support>
                                <authentication-mechanism>
                                    <authentication-mechanism-type>BasicPassword</authentication-mechanism-type>
                                    <credential-interface>javax.resource.spi.security.PasswordCredential</credential-interface>
                                </authentication-mechanism>
                                <reauthentication-support>false</reauthentication-support>
                            </outbound-resourceadapter>
                        </resourceadapter>
                    </connector>
                """,
                """
                    <connector xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
                            http://java.sun.com/xml/ns/j2ee/connector_1_6.xsd"
                        version="1.6">
                        <display-name>Generic JCA</display-name>
                        <vendor-name>adam-bien.com</vendor-name>
                        <eis-type>Generic JCA</eis-type>
                        <resourceadapter-version>1.0</resourceadapter-version>
                        <resourceadapter>
                            <outbound-resourceadapter>
                                <connection-definition>
                                    <managedconnectionfactory-class>...genericjca.GenericManagedConnectionFactory</managedconnectionfactory-class>
                                    <connectionfactory-interface>...genericjca.DataSource</connectionfactory-interface>
                                    <connectionfactory-impl-class>...genericjca.FileDataSource</connectionfactory-impl-class>
                                    <connection-interface>...genericjca.Connection</connection-interface>
                                    <connection-impl-class>...genericjca.FileConnection</connection-impl-class>
                                </connection-definition>
                                <transaction-support>LocalTransaction</transaction-support>
                                <authentication-mechanism>
                                    <authentication-mechanism-type>BasicPassword</authentication-mechanism-type>
                                    <credential-interface>javax.resource.spi.security.PasswordCredential</credential-interface>
                                </authentication-mechanism>
                                <reauthentication-support>false</reauthentication-support>
                            </outbound-resourceadapter>
                        </resourceadapter>
                    </connector>
                """
            )
        );
    }

    @Test
    void replaceVersion17Test() {
        rewriteRun(
            xml(
                """
                    <connector xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
                            http://java.sun.com/xml/ns/j2ee/connector_1_7.xsd"
                        version="1.7">
                        <display-name>Generic JCA</display-name>
                        <vendor-name>adam-bien.com</vendor-name>
                        <eis-type>Generic JCA</eis-type>
                        <resourceadapter-version>1.0</resourceadapter-version>
                        <resourceadapter>
                            <outbound-resourceadapter>
                                <connection-definition>
                                    <managedconnectionfactory-class>...genericjca.GenericManagedConnectionFactory</managedconnectionfactory-class>
                                    <connectionfactory-interface>...genericjca.DataSource</connectionfactory-interface>
                                    <connectionfactory-impl-class>...genericjca.FileDataSource</connectionfactory-impl-class>
                                    <connection-interface>...genericjca.Connection</connection-interface>
                                    <connection-impl-class>...genericjca.FileConnection</connection-impl-class>
                                </connection-definition>
                                <transaction-support>LocalTransaction</transaction-support>
                                <authentication-mechanism>
                                    <authentication-mechanism-type>BasicPassword</authentication-mechanism-type>
                                    <credential-interface>javax.resource.spi.security.PasswordCredential</credential-interface>
                                </authentication-mechanism>
                                <reauthentication-support>false</reauthentication-support>
                            </outbound-resourceadapter>
                        </resourceadapter>
                    </connector>
                """,
                """
                    <connector xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
                            http://java.sun.com/xml/ns/j2ee/connector_1_7.xsd"
                        version="1.7">
                        <display-name>Generic JCA</display-name>
                        <vendor-name>adam-bien.com</vendor-name>
                        <eis-type>Generic JCA</eis-type>
                        <resourceadapter-version>1.0</resourceadapter-version>
                        <resourceadapter>
                            <outbound-resourceadapter>
                                <connection-definition>
                                    <managedconnectionfactory-class>...genericjca.GenericManagedConnectionFactory</managedconnectionfactory-class>
                                    <connectionfactory-interface>...genericjca.DataSource</connectionfactory-interface>
                                    <connectionfactory-impl-class>...genericjca.FileDataSource</connectionfactory-impl-class>
                                    <connection-interface>...genericjca.Connection</connection-interface>
                                    <connection-impl-class>...genericjca.FileConnection</connection-impl-class>
                                </connection-definition>
                                <transaction-support>LocalTransaction</transaction-support>
                                <authentication-mechanism>
                                    <authentication-mechanism-type>BasicPassword</authentication-mechanism-type>
                                    <credential-interface>javax.resource.spi.security.PasswordCredential</credential-interface>
                                </authentication-mechanism>
                                <reauthentication-support>false</reauthentication-support>
                            </outbound-resourceadapter>
                        </resourceadapter>
                    </connector>
                """
            )
        );
    }
}

