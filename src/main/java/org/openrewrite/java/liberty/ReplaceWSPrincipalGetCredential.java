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
package org.openrewrite.java.liberty;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Preconditions;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.search.UsesMethod;
import org.openrewrite.java.tree.J;
import org.openrewrite.staticanalysis.RemoveUnneededBlock;

@Value
@EqualsAndHashCode(callSuper = false)
public class ReplaceWSPrincipalGetCredential extends Recipe {

    private static final MethodMatcher GET_CREDENTIAL = new MethodMatcher("com.ibm.websphere.security.auth.WSPrincipal getCredential()", true);

    @Override
    public String getDisplayName() {
        return "Replace `WSPrincipal.getCredential()` with `WSSubject` lookup";
    }

    @Override
    public String getDescription() {
        return "Replaces `WSCredential credential = WSPrincipal.getCredential();` with a `null` initializer + `try/catch` lookup.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return Preconditions.check(new UsesMethod<>(GET_CREDENTIAL), new JavaVisitor<ExecutionContext>() {
                    @Override
                    public J visitVariableDeclarations(J.VariableDeclarations multi, ExecutionContext ctx) {
                        J.VariableDeclarations vd = (J.VariableDeclarations) super.visitVariableDeclarations(multi, ctx);
                        if (vd.getVariables().size() != 1 || !GET_CREDENTIAL.matches(vd.getVariables().get(0).getInitializer())) {
                            return vd;
                        }
                        doAfterVisit(new RemoveUnneededBlock().getVisitor());
                        maybeRemoveImport("com.ibm.websphere.security.auth.WSPrincipal");
                        maybeAddImport("com.ibm.websphere.security.auth.WSSubject");
                        maybeAddImport("javax.security.auth.Subject");
                        String variableName = vd.getVariables().get(0).getSimpleName();
                        return JavaTemplate.builder("{\n" +
                                        "    WSCredential " + variableName + " = null;\n" +
                                        "    try {\n" +
                                        "        Subject subject = WSSubject.getCallerSubject();\n" +
                                        "        if (subject != null) {\n" +
                                        "            " + variableName + " = subject.getPublicCredentials(WSCredential.class)\n" +
                                        "                                 .iterator().next();\n" +
                                        "        }\n" +
                                        "    } catch (Exception e) {\n" +
                                        "        e.printStackTrace();\n" +
                                        "    }\n" +
                                        "}")
                                .imports(
                                        "com.ibm.websphere.security.cred.WSCredential",
                                        "com.ibm.websphere.security.auth.WSSubject",
                                        "javax.security.auth.Subject")
                                .javaParser(JavaParser.fromJavaVersion()
                                        //language=java
                                        .dependsOn(
                                                "package com.ibm.websphere.security.auth;\n" +
                                                        "public class WSSubject {\n" +
                                                        "    public static javax.security.auth.Subject getCallerSubject() { return null; }\n" +
                                                        "}",
                                                "package com.ibm.websphere.security.cred;\n" +
                                                        "public class WSCredential {}",
                                                "package javax.security.auth;\n" +
                                                        "public class Subject {\n" +
                                                        "    public <T> java.util.Set<T> getPublicCredentials(Class<T> c){ return null;}\n" +
                                                        "}"

                                        ))
                                .build()
                                .apply(getCursor(), vd.getCoordinates().replace());
                    }
                }
        );
    }
}
