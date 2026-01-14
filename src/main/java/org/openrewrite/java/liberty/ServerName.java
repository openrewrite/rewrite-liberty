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

import lombok.Getter;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.J;

public class ServerName extends Recipe {

    @Getter
    final String displayName = "Use `getProperty(\"wlp.server.name\")`";

    @Getter
    final String description = "`ServerName.getDisplayName()` is not available in Liberty.";

    private static final String SERVER_NAME = "com.ibm.websphere.runtime.ServerName";
    private static final MethodMatcher GET_DISPLAY_NAME = new MethodMatcher(SERVER_NAME + " getDisplayName()");
    private static final MethodMatcher GET_FULL_NAME = new MethodMatcher(SERVER_NAME + " getFullName()");

    private static final String ADMIN_SERVICE = "com.ibm.websphere.management.AdminService";
    private static final MethodMatcher GET_PROCESS_NAME = new MethodMatcher(ADMIN_SERVICE + " getProcessName()");

    private static final String RAS_HELPER = "com.ibm.ejs.ras.RasHelper";
    private static final MethodMatcher GET_SERVER_NAME = new MethodMatcher(RAS_HELPER + " getServerName()");

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.MethodInvocation visitMethodInvocation(J.MethodInvocation elem, ExecutionContext ctx) {
                if (GET_DISPLAY_NAME.matches(elem) || GET_FULL_NAME.matches(elem) || GET_PROCESS_NAME.matches(elem) || GET_SERVER_NAME.matches(elem)) {
                    maybeRemoveImport(SERVER_NAME);
                    maybeRemoveImport(ADMIN_SERVICE);
                    maybeRemoveImport(RAS_HELPER);
                    return JavaTemplate.builder("System.getProperty(\"wlp.server.name\")").build()
                            .apply(getCursor(), elem.getCoordinates().replace());
                }
                return super.visitMethodInvocation(elem, ctx);
            }
        };
    }
}
