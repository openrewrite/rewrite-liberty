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
import org.openrewrite.DocumentExample;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.xml.Assertions.xml;

class EJBDDNamespaceTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec
          .recipeFromResources("org.openrewrite.xml.liberty.EJBDDNamespaceRule")
          .expectedCyclesThatMakeChanges(2);
    }

    @DocumentExample
    @Test
    void replaceVersion21Test() {
        rewriteRun(
          //language=xml
          xml(
            """
              <ejb-jar xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="2.1" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/ejb-jar_2_1.xsd">
                  <display-name>
                  EchoEJBProject</display-name>
                  <enterprise-beans>
                      <session id="EchoEJB">
                          <ejb-name>EchoEJB</ejb-name>
                          <home>test.EchoEJBHome</home>
                          <remote>test.EchoEJB</remote>
                          <service-endpoint>test.EchoEJB</service-endpoint>
                          <ejb-class>test.EchoEJBBean</ejb-class>
                          <session-type>Stateless</session-type>
                          <transaction-type>Container</transaction-type>
                      </session>
                  </enterprise-beans>
              </ejb-jar>
              """,
            """
              <ejb-jar xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="2.1" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/ejb-jar_2_1.xsd">
                  <display-name>
                  EchoEJBProject</display-name>
                  <enterprise-beans>
                      <session id="EchoEJB">
                          <ejb-name>EchoEJB</ejb-name>
                          <home>test.EchoEJBHome</home>
                          <remote>test.EchoEJB</remote>
                          <service-endpoint>test.EchoEJB</service-endpoint>
                          <ejb-class>test.EchoEJBBean</ejb-class>
                          <session-type>Stateless</session-type>
                          <transaction-type>Container</transaction-type>
                      </session>
                  </enterprise-beans>
              </ejb-jar>
              """
          )
        );
    }

    @Test
    void replaceVersion30Test() {
        rewriteRun(
          //language=xml
          xml(
            """
              <ejb-jar xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="3.0" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/ejb-jar_3_0.xsd">
                  <enterprise-beans>
                      <session>
                      <ejb-name>TestBean</ejb-name>
                      <ejb-ref>
                          <ejb-ref-name>ejb/fooremote</ejb-ref-name>
                          <ejb-ref-type>Session</ejb-ref-type>
                          <remote>test.FooRemoteIF</remote>
                      </ejb-ref>
                      </session>
                  </enterprise-beans>

                  <interceptors>
                      <interceptor>
                      <interceptor-class>test.Interceptor1</interceptor-class>
                      </interceptor>
                  </interceptors>

                  <assembly-descriptor>
                      <interceptor-binding>
                      <ejb-name>*</ejb-name>
                      <interceptor-class>test.Interceptor1</interceptor-class>
                      </interceptor-binding>
                  </assembly-descriptor>
              </ejb-jar>
              """,
            """
              <ejb-jar xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="3.0" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/ejb-jar_3_0.xsd">
                  <enterprise-beans>
                      <session>
                      <ejb-name>TestBean</ejb-name>
                      <ejb-ref>
                          <ejb-ref-name>ejb/fooremote</ejb-ref-name>
                          <ejb-ref-type>Session</ejb-ref-type>
                          <remote>test.FooRemoteIF</remote>
                      </ejb-ref>
                      </session>
                  </enterprise-beans>

                  <interceptors>
                      <interceptor>
                      <interceptor-class>test.Interceptor1</interceptor-class>
                      </interceptor>
                  </interceptors>

                  <assembly-descriptor>
                      <interceptor-binding>
                      <ejb-name>*</ejb-name>
                      <interceptor-class>test.Interceptor1</interceptor-class>
                      </interceptor-binding>
                  </assembly-descriptor>
              </ejb-jar>
              """
          )
        );
    }

    @Test
    void replaceVersion31Test() {
        rewriteRun(
          //language=xml
          xml(
            """
              <ejb-jar xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="3.1" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/ejb-jar_3_1.xsd">
                  <enterprise-beans>
                      <session>
                      <ejb-name>TestBean</ejb-name>
                      <ejb-ref>
                          <ejb-ref-name>ejb/fooremote</ejb-ref-name>
                          <ejb-ref-type>Session</ejb-ref-type>
                          <remote>test.FooRemoteIF</remote>
                      </ejb-ref>
                      </session>
                  </enterprise-beans>

                  <interceptors>
                      <interceptor>
                      <interceptor-class>test.Interceptor1</interceptor-class>
                      </interceptor>
                  </interceptors>

                  <assembly-descriptor>
                      <interceptor-binding>
                      <ejb-name>*</ejb-name>
                      <interceptor-class>test.Interceptor1</interceptor-class>
                      </interceptor-binding>
                  </assembly-descriptor>
              </ejb-jar>
              """,
            """
              <ejb-jar xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="3.1" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/ejb-jar_3_1.xsd">
                  <enterprise-beans>
                      <session>
                      <ejb-name>TestBean</ejb-name>
                      <ejb-ref>
                          <ejb-ref-name>ejb/fooremote</ejb-ref-name>
                          <ejb-ref-type>Session</ejb-ref-type>
                          <remote>test.FooRemoteIF</remote>
                      </ejb-ref>
                      </session>
                  </enterprise-beans>

                  <interceptors>
                      <interceptor>
                      <interceptor-class>test.Interceptor1</interceptor-class>
                      </interceptor>
                  </interceptors>

                  <assembly-descriptor>
                      <interceptor-binding>
                      <ejb-name>*</ejb-name>
                      <interceptor-class>test.Interceptor1</interceptor-class>
                      </interceptor-binding>
                  </assembly-descriptor>
              </ejb-jar>
              """
          )
        );
    }

    @Test
    void replaceVersion32Test() {
        rewriteRun(
          //language=xml
          xml(
            """
              <ejb-jar xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/ejb-jar_3_2.xsd" version="3.2">
                  <description>Enterprise JavaBeans 3.1</description>
                  <display-name>Enterprise JavaBeans 3.1</display-name>
                  <enterprise-beans>
                          <session>
                                  <ejb-name>WorkerBean</ejb-name>
                                  <business-local>com.sample.ejb3.slsb.simple2.WorkerBusinessLocal</business-local>
                                  <business-remote>com.sample.ejb3.slsb.simple2.WorkerBusinessRemote</business-remote>
                                  <ejb-class>com.sample.ejb3.slsb.simple2.WorkerBase</ejb-class>
                                  <session-type>Stateless</session-type>
                                  <init-on-startup>true</init-on-startup>
                                  <transaction-type>Container</transaction-type>
                                  <env-entry>
                                          <env-entry-name>FILTER</env-entry-name>
                                          <env-entry-type>java.lang.Long</env-entry-type>
                                          <env-entry-value>11011</env-entry-value>
                                  </env-entry>
                          </session>
                  </enterprise-beans>
              </ejb-jar>
              """,
            """
              <ejb-jar xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/ejb-jar_3_2.xsd" version="3.2">
                  <description>Enterprise JavaBeans 3.1</description>
                  <display-name>Enterprise JavaBeans 3.1</display-name>
                  <enterprise-beans>
                          <session>
                                  <ejb-name>WorkerBean</ejb-name>
                                  <business-local>com.sample.ejb3.slsb.simple2.WorkerBusinessLocal</business-local>
                                  <business-remote>com.sample.ejb3.slsb.simple2.WorkerBusinessRemote</business-remote>
                                  <ejb-class>com.sample.ejb3.slsb.simple2.WorkerBase</ejb-class>
                                  <session-type>Stateless</session-type>
                                  <init-on-startup>true</init-on-startup>
                                  <transaction-type>Container</transaction-type>
                                  <env-entry>
                                          <env-entry-name>FILTER</env-entry-name>
                                          <env-entry-type>java.lang.Long</env-entry-type>
                                          <env-entry-value>11011</env-entry-value>
                                  </env-entry>
                          </session>
                  </enterprise-beans>
              </ejb-jar>
              """
          )
        );
    }

    @Test
    void replaceVersion33Test() {
        rewriteRun(
          //language=xml
          xml(
            """
              <ejb-jar xmlns="http://java.sun.com/xml/ns/j2ee" version="3.3">
                  <description>Enterprise JavaBeans 3.1</description>
                  <display-name>Enterprise JavaBeans 3.1</display-name>
                  <enterprise-beans>
                          <session>
                                  <ejb-name>WorkerBean</ejb-name>
                                  <business-local>com.sample.ejb3.slsb.simple2.WorkerBusinessLocal</business-local>
                                  <business-remote>com.sample.ejb3.slsb.simple2.WorkerBusinessRemote</business-remote>
                                  <ejb-class>com.sample.ejb3.slsb.simple2.WorkerBase</ejb-class>
                                  <session-type>Stateless</session-type>
                                  <init-on-startup>true</init-on-startup>
                                  <transaction-type>Container</transaction-type>
                                  <env-entry>
                                          <env-entry-name>FILTER</env-entry-name>
                                          <env-entry-type>java.lang.Long</env-entry-type>
                                          <env-entry-value>11011</env-entry-value>
                                  </env-entry>
                          </session>
                  </enterprise-beans>
              </ejb-jar>
              """,
            """
              <ejb-jar xmlns="http://xmlns.jcp.org/xml/ns/javaee" version="3.3">
                  <description>Enterprise JavaBeans 3.1</description>
                  <display-name>Enterprise JavaBeans 3.1</display-name>
                  <enterprise-beans>
                          <session>
                                  <ejb-name>WorkerBean</ejb-name>
                                  <business-local>com.sample.ejb3.slsb.simple2.WorkerBusinessLocal</business-local>
                                  <business-remote>com.sample.ejb3.slsb.simple2.WorkerBusinessRemote</business-remote>
                                  <ejb-class>com.sample.ejb3.slsb.simple2.WorkerBase</ejb-class>
                                  <session-type>Stateless</session-type>
                                  <init-on-startup>true</init-on-startup>
                                  <transaction-type>Container</transaction-type>
                                  <env-entry>
                                          <env-entry-name>FILTER</env-entry-name>
                                          <env-entry-type>java.lang.Long</env-entry-type>
                                          <env-entry-value>11011</env-entry-value>
                                  </env-entry>
                          </session>
                  </enterprise-beans>
              </ejb-jar>
              """
          )
        );
    }
}
