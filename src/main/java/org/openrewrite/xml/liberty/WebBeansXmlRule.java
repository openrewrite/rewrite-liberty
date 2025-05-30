/*
 * Copyright 2025 the original author or authors.
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
/*
 * Copyright 2025 the original author or authors.
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

import org.openrewrite.*;
import org.openrewrite.xml.ChangeTagAttribute;
import org.openrewrite.xml.ChangeTagName;
import org.openrewrite.xml.XmlVisitor;
import org.openrewrite.xml.tree.Xml;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class WebBeansXmlRule extends Recipe {

    @Override
    public String getDisplayName() {
        return "Replace beans.xml file";
    }

    @Override
    public String getDescription() {
        return "This Recipe replaces OpenWebBeans schema in every beans.xml with the standard CDI schema.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return Preconditions.check(
                new FindSourceFiles("**/beans.xml"),
                new XmlVisitor<ExecutionContext>() {
                    @Override
                    public Xml visitTag(Xml.Tag tag, ExecutionContext ctx) {
                        Xml.Tag t = (Xml.Tag) super.visitTag(tag, ctx);

                        if ("WebBeans".equals(t.getName())) {
                            Map<String, String> attrs = t.getAttributes().stream()
                                    .collect(toMap(
                                            Xml.Attribute::getKeyAsString,
                                            Xml.Attribute::getValueAsString
                                    ));
                            String xmlns = attrs.get("xmlns");

                            if ("urn:java:ee".equalsIgnoreCase(xmlns)) {

                                doAfterVisit(new ChangeTagName(
                                        "WebBeans",
                                        "beans"
                                ).getVisitor());

                                doAfterVisit(new ChangeTagAttribute(
                                        "beans",
                                        "xmlns",
                                        "http://xmlns.jcp.org/xml/ns/javaee",
                                        "urn:java:ee",
                                        null
                                ).getVisitor());

                                doAfterVisit(new ChangeTagAttribute(
                                        "beans",
                                        "xsi:schemaLocation",
                                        "http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd",
                                        null,
                                        null
                                ).getVisitor());
                            }
                        }
                        return t;
                    }
                }
        );
    }

}
