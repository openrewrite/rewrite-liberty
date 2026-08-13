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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openrewrite.Recipe;
import org.openrewrite.config.CategoryDescriptor;
import org.openrewrite.config.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

class RecipeMetadataTest {

    static List<String> recipePackages() {
        return Arrays.asList(
          "org.openrewrite.java.liberty",
          "org.openrewrite.xml.liberty",
          "org.openrewrite.maven.liberty");
    }

    @ParameterizedTest
    @MethodSource("recipePackages")
    void everyRecipeIsTaggedLiberty(String packageName) {
        List<Recipe> recipes = recipesIn(packageName);
        assertThat(recipes).as("No recipes found in %s", packageName).isNotEmpty();
        assertThat(recipes).allSatisfy(recipe -> assertThat(recipe.getTags())
          .as(recipe.getName())
          .contains("liberty"));
    }

    @ParameterizedTest
    @MethodSource("recipePackages")
    void everyRecipePackageDeclaresACategory(String packageName) {
        assertThat(libertyCategories())
          .as("category.yml declares no category for %s, so the docs fall back to a synthetic one", packageName)
          .anySatisfy(category -> assertThat(category.getPackageName()).isEqualTo(packageName));
    }

    @Test
    void everyDeclaredCategoryHasRecipes() {
        Set<String> packagesWithRecipes = recipePackages().stream()
          .filter(packageName -> !recipesIn(packageName).isEmpty())
          .collect(toSet());
        assertThat(libertyCategories()).allSatisfy(category -> assertThat(packagesWithRecipes)
          .as("category.yml declares a category for %s, which contains no recipes", category.getPackageName())
          .contains(category.getPackageName()));
    }

    private static List<Recipe> recipesIn(String packageName) {
        return Environment.builder()
          .scanRuntimeClasspath(packageName)
          .build()
          .listRecipes().stream()
          .filter(recipe -> recipe.getName().startsWith(packageName))
          .collect(toList());
    }

    private static List<CategoryDescriptor> libertyCategories() {
        return Environment.builder()
          .scanRuntimeClasspath(recipePackages().toArray(new String[0]))
          .build()
          .listCategoryDescriptors().stream()
          .filter(category -> category.getPackageName().endsWith(".liberty"))
          .collect(toList());
    }
}
