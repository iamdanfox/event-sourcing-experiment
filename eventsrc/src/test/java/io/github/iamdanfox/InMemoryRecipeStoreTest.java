/*
 * Copyright 2017 Palantir Technologies, Inc. All rights reserved.
 */

package io.github.iamdanfox;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.Test;

public class InMemoryRecipeStoreTest {

    @Test
    public void returns_empty_for_lookup_by_id_initially() {
        RecipeStore store = new InMemoryRecipeStore();
        assertThat(store.getRecipeById(Literals.ID), is(Optional.empty()));
    }

    @Test
    public void created_event_makes_lookup_succeed() {
        RecipeStore store = prefilled1();
        assertThat(store.getRecipeById(Literals.ID), is(Optional.of(Literals.RECIPE_RESPONSE)));
    }

    @Test
    public void add_tag_event_means_response_contains_that_tag() {
        InMemoryRecipeStore store = prefilled1();

        store.match(Literals.ADD_TAG);

        RecipeResponse response = store.getRecipeById(Literals.ID).get();
        assertThat(response.tags(), contains(Literals.TAG));
    }

    @Test
    public void remove_tag_event_means_response_contains_that_tag() {
        WritableRecipeStore store = prefilled1();

        Stream.of(Literals.ADD_TAG, Literals.ADD_TAG, Literals.REMOVE_TAG, Literals.REMOVE_TAG)
                .forEach(store::consume);

        RecipeResponse response = store.getRecipeById(Literals.ID).get();
        assertThat(response.tags(), is(empty()));
    }

    private static InMemoryRecipeStore prefilled1() {
        InMemoryRecipeStore store = new InMemoryRecipeStore();
        store.match(RecipeCreatedEvent.builder()
                .id(Literals.ID)
                .create(CreateRecipe.builder()
                        .contents("recipe contents")
                        .build())
                .build());
        return store;
    }
}
