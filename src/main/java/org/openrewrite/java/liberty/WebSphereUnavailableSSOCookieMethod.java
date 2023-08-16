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
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.J;

@Value
@EqualsAndHashCode(callSuper = true)
public class WebSphereUnavailableSSOCookieMethod extends Recipe {

    @Override
    public String getDisplayName() {
        return "Replace `revokeSSOCookies` with `logout`";
    }

    @Override
    public String getDescription() {
        return "Replace `WSSecurityHelper.revokeSSOCookies(request, response)` with `request.logout()`.";
    }

    private static final String WSSECURITY_HELPER = "com.ibm.websphere.security.WSSecurityHelper";
    private static final MethodMatcher METHOD_PATTERN = new MethodMatcher(WSSECURITY_HELPER + " revokeSSOCookies(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)");

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaVisitor<ExecutionContext>() {
            @Override
            public J visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
                if (METHOD_PATTERN.matches(method)) {
                    maybeRemoveImport(WSSECURITY_HELPER);
                    return JavaTemplate.builder("#{any()}.logout()")
                            .javaParser(JavaParser.fromJavaVersion().classpathFromResources(ctx, "websecurity_logout_test"))
                            .build()
                            .apply(getCursor(), method.getCoordinates().replace(), method.getArguments().get(0));
                }
                return super.visitMethodInvocation(method, ctx);
            }
        };
    }
}
