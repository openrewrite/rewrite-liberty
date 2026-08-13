/*
 * Copyright 2026 the original author or authors.
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

package org.openrewrite.liberty;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openrewrite.Recipe;
import org.openrewrite.config.Environment;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RecipeTagsTest {

    @ParameterizedTest
    @ValueSource(strings = {
      "org.openrewrite.java.liberty",
      "org.openrewrite.xml.liberty",
      "org.openrewrite.maven.liberty"
    })
    void everyRecipeIsTaggedLiberty(String packageName) {
        List<Recipe> recipes = Environment.builder()
          .scanRuntimeClasspath(packageName)
          .build()
          .listRecipes().stream()
          .filter(recipe -> recipe.getName().startsWith(packageName))
          .collect(Collectors.toList());
        assertThat(recipes).as("No recipes found in %s", packageName).isNotEmpty();
        assertThat(recipes).allSatisfy(recipe -> assertThat(recipe.getTags())
          .as(recipe.getName())
          .contains("liberty"));
    }
}
