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
import org.openrewrite.ScanningRecipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RemoveWas2LibertyNonPortableJndiLookup extends ScanningRecipe<Set<JavaType.Variable>> {
    final private String INITIAL_PROPERTY = "java.naming.factory.initial";
    final private String URL_PROPERTY = "java.naming.provider.url";

    @Override
    public String getDisplayName() {
        return "Removes invalid JNDI properties";
    }

    @Override
    public String getDescription() {
        return "Remove the use of invalid JNDI properties from Hashtable.";
    }

    @Override
    public Set<JavaType.Variable> getInitialValue(ExecutionContext ctx) {
        return new HashSet<>();
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getScanner(Set<JavaType.Variable> acc) {

        return new JavaIsoVisitor<ExecutionContext>() {

            @Override
            public J.VariableDeclarations visitVariableDeclarations(J.VariableDeclarations vd, ExecutionContext ctx) {
                for (J.VariableDeclarations.NamedVariable variable : vd.getVariables()) {
                    checkForPropertiesVariable(variable.getVariableType(), variable.getInitializer());
                }
                return vd;
            }

            @Override
            public J.Assignment visitAssignment(J.Assignment assignment, ExecutionContext ctx) {
                JavaType.Variable variable = ((J.Identifier) assignment.getVariable()).getFieldType();
                if (!checkForPropertiesVariable(variable, assignment.getAssignment())) {
                    // If present, remove the variable from the accumulator since it was reassigned to an unrelated value
                    acc.remove(variable);
                }
                return assignment;
            }

            // Add a variable to the accumulator if it matches either property
            private boolean checkForPropertiesVariable(JavaType.Variable variable, Expression value) {
                if (value instanceof J.Literal) {
                    String stringValue = ((J.Literal) value).toString();
                    if (stringValue.equals(INITIAL_PROPERTY) || stringValue.equals(URL_PROPERTY)) {
                        acc.add(variable);
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor(Set<JavaType.Variable> acc) {
        MethodMatcher methodMatcher = new MethodMatcher("java.util.Hashtable put(..)", false);


        return new JavaIsoVisitor<ExecutionContext>() {

            @Override
            public J.MethodInvocation visitMethodInvocation(J.MethodInvocation mi, ExecutionContext ctx) {
                // Return if this method does not match Hashtable.put()
                if (!methodMatcher.matches(mi)) {
                    return mi;
                }

                Expression firstArgument = mi.getArguments().get(0);
                if (firstArgument instanceof J.Literal) {
                    // Return if the first argument is a literal and does not match either property
                    J.Literal literalExp = (J.Literal) firstArgument;
                    Object value = literalExp.getValue();
                    if (!value.equals(INITIAL_PROPERTY) && !value.equals(URL_PROPERTY)) {
                        return mi;
                    }
                } else if (firstArgument instanceof J.Identifier) {
                    // Return if the first argument is a variable and does not match the type of any collected variables
                    if (!acc.contains(((J.Identifier) firstArgument).getFieldType())) {
                        return mi;
                    }
                }

                return null;
            }
        };
    }
}
