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

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.ShortenFullyQualifiedTypeReferences;
import org.openrewrite.java.tree.J;

public class ServerName extends Recipe {

    @Override
    public String getDisplayName() {
        return "Use `getProperty(\"wlp.server.name\")`";
    }

    @Override
    public String getDescription() {
        return "`ServerName.getDisplayName()` is not available in Liberty.";
    }

    private static final String SERVER_NAME = "com.ibm.websphere.runtime.ServerName";
    private static final MethodMatcher getDisplayName = new MethodMatcher(SERVER_NAME + " getDisplayName()");
    private static final MethodMatcher getFullName = new MethodMatcher(SERVER_NAME + " getFullName()");

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaVisitor<ExecutionContext>() {
            @Override
            public J visitMethodInvocation(J.MethodInvocation elem, ExecutionContext ctx) {
                if (getDisplayName.matches(elem) || getFullName.matches(elem)) {
                    maybeRemoveImport(SERVER_NAME);
                    doAfterVisit(new ShortenFullyQualifiedTypeReferences().getVisitor());
                    return JavaTemplate.builder("System.getProperty(\"wlp.server.name\")").build()
                            .apply(getCursor(), elem.getCoordinates().replace());
                }
                return super.visitMethodInvocation(elem, ctx);
            }
        };
    }
}

