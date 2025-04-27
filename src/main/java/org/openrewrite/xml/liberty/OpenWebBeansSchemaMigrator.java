package org.openrewrite.xml.liberty;

import org.openrewrite.*;
import org.openrewrite.xml.ChangeTagAttribute;
import org.openrewrite.xml.ChangeTagName;
import org.openrewrite.xml.XmlVisitor;
import org.openrewrite.xml.tree.Xml;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class OpenWebBeansSchemaMigrator extends Recipe {

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
