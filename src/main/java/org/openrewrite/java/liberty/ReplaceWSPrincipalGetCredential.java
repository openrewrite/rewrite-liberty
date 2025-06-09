package org.openrewrite.java.liberty;

import lombok.EqualsAndHashCode;
import lombok.Value;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Preconditions;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;

import org.openrewrite.java.*;
import org.openrewrite.java.search.UsesMethod;
import org.openrewrite.java.tree.J;
import org.openrewrite.staticanalysis.RemoveUnneededBlock;

@Value
@EqualsAndHashCode(callSuper = false)
public class ReplaceWSPrincipalGetCredential extends Recipe {

    private static final MethodMatcher GET_CREDENTIAL =
            new MethodMatcher(
                    "com.ibm.websphere.security.auth.WSPrincipal getCredential()", true);

    @Override
    public String getDisplayName() {
        return "Replace WSPrincipal.getCredential() with WSSubject lookup";
    }

    @Override
    public String getDescription() {
        return "Replaces `WSCredential credential = WSPrincipal.getCredential();` with a null-init + try/catch lookup.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return Preconditions.check(
                new UsesMethod<>(GET_CREDENTIAL),
                new JavaVisitor<ExecutionContext>() {
                    private static final String TEMPLATE = ""
                            + "{\n"
                            + "    WSCredential credential = null;\n"
                            + "    try {\n"
                            + "        javax.security.auth.Subject subject = WSSubject.getCallerSubject();\n"
                            + "        if (subject != null) {\n"
                            + "            credential = subject.getPublicCredentials(WSCredential.class)\n"
                            + "                                 .iterator().next();\n"
                            + "        }\n"
                            + "    } catch (Exception e) {\n"
                            + "        e.printStackTrace();\n"
                            + "    }\n"
                            + "}";

                    private final JavaTemplate blockTemplate = JavaTemplate.builder(TEMPLATE)
                            .contextSensitive()
                            .imports(

                                    "com.ibm.websphere.security.cred.WSCredential"
                            )
                            .build();

                    @Override
                    public J visitVariableDeclarations(J.VariableDeclarations multi, ExecutionContext ctx) {
                        J after = super.visitVariableDeclarations(multi, ctx);
                        if (!(after instanceof J.VariableDeclarations)) {
                            return after;
                        }
                        J.VariableDeclarations vd = (J.VariableDeclarations) after;
                        if (vd.getVariables().size() != 1) {
                            return vd;
                        }
                        J.VariableDeclarations.NamedVariable nv = vd.getVariables().get(0);
                        if (!(nv.getInitializer() instanceof J.MethodInvocation)) {
                            return vd;
                        }
                        J.MethodInvocation mi = (J.MethodInvocation) nv.getInitializer();
                        if (!GET_CREDENTIAL.matches(mi)) {
                            return vd;
                        }

                        // 1) Replace the one declaration with our single-block template
                        J replaced = blockTemplate.apply(
                                getCursor(),
                                vd.getCoordinates().replace()
                        );

                        doAfterVisit(new RemoveUnneededBlock().getVisitor());
                        doAfterVisit(new AddImport<>("com.ibm.websphere.security.auth.WSSubject",null,false));
                        doAfterVisit(new RemoveImport<>("com.ibm.websphere.security.auth.WSPrincipal"));
                        doAfterVisit(
                                ShortenFullyQualifiedTypeReferences.modifyOnly(replaced)
                        );
                        return replaced;
                    }
                }
        );
    }
}
