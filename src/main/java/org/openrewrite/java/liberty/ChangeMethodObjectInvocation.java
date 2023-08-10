package org.openrewrite.java.liberty;

import lombok.EqualsAndHashCode;
import lombok.Value;

import org.openrewrite.*;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/*
    Development in this class is not complete
*/
@Value
@EqualsAndHashCode(callSuper = true)
public class ChangeMethodObjectInvocation extends Recipe {

    @JsonCreator
    public ChangeMethodObjectInvocation() {

    }

    @Override
    public String getDisplayName() {
        return "Change method invocation for revokeSSOCookies in Websphere";
    }

    @Override
    public String getDescription() {
        return "This recipe substitutes revokeCookies to logout.";
    }

    @Override
    public boolean causesAnotherCycle() {
        return true;
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new ChangeMethodObjectInvocationVisitor();
    }

    private class ChangeMethodObjectInvocationVisitor extends JavaVisitor<ExecutionContext> {
        private final MethodMatcher methodMatcher;
        private String origPattern = "com.ibm.websphere.security.WSSecurityHelper revokeSSOCookies(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)";

        public ChangeMethodObjectInvocationVisitor() {

            this.methodMatcher = new MethodMatcher(origPattern);
        }

        @Override
        public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
            J.MethodInvocation m = method;

            // check if the pattern of request matches ,retrieve the first argument
            if (methodMatcher.matches(method)) {
                String httpreqVar = m.getArguments().get(0).toString();
                String temp = httpreqVar + ".logout()";
                // JavaTemplate addArgTemplate = JavaTemplate.builder(temp.strip()).contextSensitive().build();
                // m = addArgTemplate.apply(getCursor(), m.getCoordinates().replace());
                // maybeRemoveImport("com.ibm.websphere.security.WSSecurityHelper");
                JavaTemplate addArgTemplate = JavaTemplate.builder("#{any()}.logout()").contextSensitive().build();
                maybeRemoveImport("com.ibm.websphere.security.WSSecurityHelper");
                m = addArgTemplate.apply(getCursor(), m.getCoordinates().replace(), m.getArguments().get(0));
            }
            return m;
        }
    }
}
