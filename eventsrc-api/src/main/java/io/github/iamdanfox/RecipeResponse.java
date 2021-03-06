/*
 * Copyright 2017 Palantir Technologies, Inc. All rights reserved.
 */

package io.github.iamdanfox;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRecipeResponse.class)
public interface RecipeResponse {

    RecipeId id();

    String contents();

    Set<RecipeTag> tags();

    static ImmutableRecipeResponse.Builder builder() {
        return ImmutableRecipeResponse.builder();
    }
}
