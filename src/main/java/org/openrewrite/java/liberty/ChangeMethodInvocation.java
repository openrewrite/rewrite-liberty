package org.openrewrite.java.liberty;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrewrite.*;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.*;
import org.openrewrite.java.tree.JavaType.FullyQualified;
import org.openrewrite.marker.Markers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
    Development in this class is not complete
*/
@Value
@EqualsAndHashCode(callSuper = true)
public class ChangeMethodInvocation extends Recipe {

    @Option(displayName = "Method pattern",
            description = "A method pattern that is used to find matching method invocations.",
            example = "org.mockito.Matchers anyVararg()")
    @NonNull
    String methodPattern;

    @Option(displayName = "New method name",
            description = "The method name that will replace the existing name.",
            example = "org.mockito.Matchers any()")
    @NonNull
    String newMethodPattern;

    @Option(displayName = "Perform static methd call",
            description = "When set to `true` the method invocation will be performed statically.",
            example = "true",
            required = false)
    @Nullable
    Boolean performStaticCall;

    @JsonCreator
    public ChangeMethodInvocation(@NonNull @JsonProperty("methodPattern") String methodPattern, 
    @NonNull @JsonProperty("newMethodPattern") String newMethodPattern, @Nullable @JsonProperty("performStaticCall") Boolean performStaticCall) {
        this.methodPattern = methodPattern;
        this.newMethodPattern = newMethodPattern;
        this.performStaticCall = performStaticCall;
    }


    @Override
    public String getDisplayName() {
        return "Change method invocation";
    }

    @Override
    public String getDescription() {
        return "Rename a method invocation.";
    }

    @Override
    public boolean causesAnotherCycle() {
        return true;
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new ChangeMethodInvocationVisitor(new MethodMatcher(methodPattern, false), newMethodPattern);
    }

    private class ChangeMethodInvocationVisitor extends JavaVisitor<ExecutionContext> {
        private final MethodMatcher methodMatcher;
        private MethodMatcher methodReplacmentMatcher;
        private String oldMethodType;
        
        private String newMethodType;
        private String newMethodOwnerName;
        private String newMethodName;
        private String newMethodsArgsStr;
        @Nullable
        private JavaParser.Builder<?, ?> javaParser;

        Pattern methodPatternMatcher = Pattern.compile("([^\\s]+)\\s+([a-zA-Z0-9]+)\\((.*)\\)");

        private ChangeMethodInvocationVisitor(MethodMatcher methodMatcher, String newMethodPattern) {
            this.methodMatcher = methodMatcher;
            this.oldMethodType = methodMatcher.getTargetTypePattern().pattern().replace("[.$]", ".");

            if (newMethodPattern != null) {
                Matcher m = methodPatternMatcher.matcher(newMethodPattern);
                if(m.find()) {
                    this.newMethodType = m.group(1);
                    if(this.newMethodType != null) {
                        String[] parts = this.newMethodType.split("\\.");
                        this.newMethodOwnerName = parts[parts.length-1];
                    }

                    this.newMethodName = m.group(2);
                    this.newMethodsArgsStr = m.group(3);
                    if(this.newMethodsArgsStr != null) {
                        StringBuilder newArgs = new StringBuilder();
                        int argSize = newMethodsArgsStr.split(",").length;
                        for(int i=1; i <= argSize; i++) {
                            newArgs.append("*");
                            if (i < argSize) {
                                newArgs.append(",");
                            }
                        }
                        this.methodReplacmentMatcher = new MethodMatcher(newMethodPattern.replace(this.newMethodsArgsStr, newArgs), false);
                    } else {
                        this.newMethodsArgsStr = "";
                        this.methodReplacmentMatcher = new MethodMatcher(newMethodPattern, false);
                    }
                }
            }
        }
        private JavaParser.Builder<?, ?> javaParser(ExecutionContext ctx) {
            if (javaParser == null) {
                javaParser = JavaParser.fromJavaVersion()
                        .classpathFromResources(ctx, "websecurity_test");
            }
            return javaParser;
        }

        @Override
        public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
            J.MethodInvocation m = method;

            if (methodReplacmentMatcher != null && methodMatcher.matches(method) && !methodReplacmentMatcher.matches(method)) {
                if (performStaticCall) {
                    String temp = newMethodType+"."+newMethodName+"("+newMethodsArgsStr+");";
                    JavaTemplate addArgTemplate = JavaTemplate.builder(temp)
                                                                .javaParser(javaParser(ctx))
                                                                .build();
                    J.MethodInvocation qualifiedInvocation = addArgTemplate.apply(getCursor(), m.getCoordinates().replace());
                    temp = newMethodOwnerName+"."+newMethodName+"("+newMethodsArgsStr+");";
                   
                    addArgTemplate = JavaTemplate.builder(temp)
                                                    .javaParser(javaParser(ctx))
                                                    .build();
                    J.MethodInvocation simpleInvocation = addArgTemplate.apply(getCursor(), m.getCoordinates().replace());
                    m = simpleInvocation.withMethodType(qualifiedInvocation.getMethodType());
                    m = m.withSelect(m.getSelect().withType(qualifiedInvocation.getSelect().getType()));

                    maybeAddImport(m.getMethodType().getDeclaringType().getFullyQualifiedName(), false);
                    maybeRemoveImport(oldMethodType);
                 
                } else {
                    JavaType.Method type = m.getMethodType();
                    if (type != null) {
                        type = type.withName(newMethodName);
                        FullyQualified declaringType = type.getDeclaringType();
                        String previousMethodType = declaringType.getFullyQualifiedName();
                        declaringType = declaringType.withFullyQualifiedName(newMethodType);
                        type = type.withDeclaringType(declaringType);
                        
                        //add new import and remove old if no longer used
                        maybeAddImport(newMethodType);
                        maybeRemoveImport(previousMethodType);
                    }
                    m = m.withName(m.getName().withSimpleName(newMethodName))
                            .withMethodType(type);
                    
                    if(this.newMethodsArgsStr == null) {
                        // clear arguments
                        List<Expression> methodArgs = Collections.singletonList(new J.Empty(Tree.randomId(), Space.EMPTY, Markers.EMPTY));
                        m = m.withArguments(methodArgs);
                    } else {
                        // override arguments
                        JavaTemplate addArgTemplate = JavaTemplate.builder( newMethodsArgsStr).build();
                        m = addArgTemplate.apply(getCursor(), m.getCoordinates().replaceArguments());
                    }
                }
            }
            return m;
        }
    }
}
