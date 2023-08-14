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

import lombok.EqualsAndHashCode;
import lombok.Value;

import org.openrewrite.*;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.*;

import com.fasterxml.jackson.annotation.JsonCreator;


@Value
@EqualsAndHashCode(callSuper = true)
public class ChangeMethodObjectInvocation extends Recipe {

    @JsonCreator
    public ChangeMethodObjectInvocation() {

    }

    @Override
    public String getDisplayName() {
        return "Replace `revokeSSOCookies` with `logout`";
    }

    @Override
    public String getDescription() {
        return "Replace `WSSecurityHelper.revokeSSOCookies(request, response)` with `request.logout()`.";
    }

    @Override
    public boolean causesAnotherCycle() {
        return true;
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new ChangeMethodObjectInvocationVisitor();
    }
    @Nullable
       

    private class ChangeMethodObjectInvocationVisitor extends JavaVisitor<ExecutionContext> {
        private JavaParser.Builder<?, ?> javaParser;
        private static final String WSSECURITY_HELPER = "com.ibm.websphere.security.WSSecurityHelper";
        private  MethodMatcher METHOD_PATTERN = new MethodMatcher(WSSECURITY_HELPER + " revokeSSOCookies(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)");

        public ChangeMethodObjectInvocationVisitor() {

            this.METHOD_PATTERN = new MethodMatcher(WSSECURITY_HELPER + " revokeSSOCookies(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)");
        }
        private JavaParser.Builder<?, ?> javaParser(ExecutionContext ctx) {
            if (javaParser == null) {
                javaParser = JavaParser.fromJavaVersion()
                        .classpathFromResources(ctx, "websecurity_logout_test");
            }
            return javaParser;
        }

        @Override
        public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
            J.MethodInvocation m = method;
            if (METHOD_PATTERN.matches(method)) {
                JavaTemplate addArgTemplate = JavaTemplate.builder("#{any()}.logout()").javaParser(javaParser(ctx)).contextSensitive().build();
                maybeRemoveImport("com.ibm.websphere.security.WSSecurityHelper");
                m = addArgTemplate.apply(getCursor(), m.getCoordinates().replace(), m.getArguments().get(0));
            }
            return m;
        }
    }
}