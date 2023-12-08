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


import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.MethodCall;

public class RemoveWas2LibertyNonPortableJndiLookup extends Recipe {

    @Override
    public String getDisplayName() {
        return "Removes invalid JNDI properties";
    }

    @Override
    public String getDescription() {
        return "Remove the use of invalid JNDI properties from Hashtable.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MethodInvocationVisitor();
    }

    private class MethodInvocationVisitor extends JavaVisitor<ExecutionContext> {
        MethodMatcher methodMatcher = new MethodMatcher("java.util.Hashtable put(java.lang.Object, java.lang.Object)", false);

        @SuppressWarnings("NullableProblems")
        @Nullable
        @Override
        public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
            return visitMethodCall(method);
        }

        @Nullable
        private <M extends MethodCall> M visitMethodCall(M methodCall) {
            if (!methodMatcher.matches(methodCall)) {
                return methodCall;
            }
            J.Block parentBlock = getCursor().firstEnclosing(J.Block.class);
            //noinspection SuspiciousMethodCalls
            if (parentBlock != null && !parentBlock.getStatements().contains(methodCall)) {
                return methodCall;
            }
            // Remove the method invocation when the argumentMatcherPredicate is true for all arguments
            Expression firstArg = methodCall.getArguments().get(0);
            if (firstArg instanceof J.Literal) {
                J.Literal literalExp = (J.Literal) firstArg;
                Object value = literalExp.getValue();
                if (!value.equals("java.naming.factory.initial") && !value.equals("java.naming.provider.url")) {
                    return methodCall;
                }
            } else {
                return methodCall;
            }

            if (methodCall.getMethodType() != null) {
                maybeRemoveImport(methodCall.getMethodType().getDeclaringType());
            }
            return null;
        }
    }

}
