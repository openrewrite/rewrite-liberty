package org.openrewrite.java.liberty;

import java.util.List;
import java.util.stream.Collectors;

import org.intellij.lang.annotations.Language;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;

import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.tree.J;
import org.openrewrite.SourceFile;

public class ReplaceWSPrincipalGetCredential extends Recipe {

    @Override
    public String getDisplayName() {
        return "Replace WSPrincipal.getCredential() implementation";
    }

    @Override
    public String getDescription() {
        return "Replace a public zero-arg `WSCredential getCredential()` with WSSubject.getCallerSubject() logic.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {

            private final JavaParser parser = JavaParser.fromJavaVersion().build();

            @Override
            public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration md, ExecutionContext ctx) {
                md = super.visitMethodDeclaration(md, ctx);

                if (!"getCredential".equals(md.getSimpleName())) {
                    return md;
                }

                boolean isPublic = md.getModifiers().stream()
                        .anyMatch(m -> m.getType().equals(J.Modifier.Type.Public));
                if (!isPublic) {
                    return md;
                }

                if (md.getReturnTypeExpression() == null) {
                    return md;
                }
                String returnType = md.getReturnTypeExpression().printTrimmed();
                if (!returnType.endsWith("WSCredential")) {
                    return md;
                }

                @Language("java")
                String dummy =
                        "class Dummy {\n" +
                                "    public com.ibm.websphere.security.cred.WSCredential getCredential() {\n" +
                                "        com.ibm.websphere.security.cred.WSCredential credential = null;\n" +
                                "        try {\n" +
                                "            javax.security.auth.Subject subject =\n" +
                                "                com.ibm.websphere.security.auth.WSSubject.getCallerSubject();\n" +
                                "            if (subject != null) {\n" +
                                "                credential = subject.getPublicCredentials(\n" +
                                "                    com.ibm.websphere.security.cred.WSCredential.class\n" +
                                "                ).iterator().next();\n" +
                                "            }\n" +
                                "        } catch (Exception e) {\n" +
                                "            e.printStackTrace();\n" +
                                "        }\n" +
                                "        return credential;\n" +
                                "    }\n" +
                                "}";

                List<SourceFile> parsed = parser.parse(dummy).collect(Collectors.toList());
                J.Block newBody = null;
                for (SourceFile sf : parsed) {
                    if (!(sf instanceof J.CompilationUnit)) {
                        continue;
                    }
                    J.CompilationUnit cu = (J.CompilationUnit) sf;
                    for (J.ClassDeclaration cd : cu.getClasses()) {
                        if (!"Dummy".equals(cd.getSimpleName())) {
                            continue;
                        }
                        for (Object stmt : cd.getBody().getStatements()) {
                            if (stmt instanceof J.MethodDeclaration) {
                                J.MethodDeclaration dmd = (J.MethodDeclaration) stmt;
                                if ("getCredential".equals(dmd.getSimpleName()) && dmd.getBody() != null) {
                                    newBody = dmd.getBody();
                                    break;
                                }
                            }
                        }
                        if (newBody != null) {
                            break;
                        }
                    }
                    if (newBody != null) {
                        break;
                    }
                }
                if (newBody == null) {
                    return md;
                }

                return md.withBody(newBody);
            }
        };
    }
}
