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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Value
@EqualsAndHashCode(callSuper = true)
public class ChangeStringLiteral extends Recipe {

    @Option(displayName = "Value pattern",
            description = "A pattern that is used to find matching string literal.",
            example = "java:(.*)")
    @NonNull
    String valuePattern;

    @Option(displayName = "New value template",
            description = "The template that will replace the existing value.",
            example = "org:$1")
    @NonNull
    String newValueTemplate;

    @JsonCreator
    public ChangeStringLiteral(@NonNull @JsonProperty("valuePattern") String valuePattern, @NonNull @JsonProperty("newValueTemplate") String newValueTemplate) {
        this.valuePattern = valuePattern;
        this.newValueTemplate = newValueTemplate;
    }


    @Override
    public String getDisplayName() {
        return "Change string literal";
    }

    @Override
    public String getDescription() {
        return "Changes the value of a string literal.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new ChangeStringLiteralVisitor<> (Pattern.compile(valuePattern), newValueTemplate);
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public class ChangeStringLiteralVisitor<P> extends JavaIsoVisitor<P> {
        Pattern valuePattern;
        String newValueTemplate;

        @Override
        public J.Literal visitLiteral(J.Literal literal, P p) {
            String literalValue = literal.getValue().toString();
            Matcher m = valuePattern.matcher(literalValue);
            if (m.find()) {
                literalValue =  m.replaceFirst(newValueTemplate);
                literal = literal.withValue(literalValue).withValueSource(literalValue);
            }
            return literal;
        }
    }

}