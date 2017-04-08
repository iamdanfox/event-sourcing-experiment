/*
 * Copyright 2017 Palantir Technologies, Inc. All rights reserved.
 */

package io.github.iamdanfox;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;

public class BlockingRecipeStoresTest {

    Event event = mock(Event.class);
    WritableRecipeStore underlyingStore = mock(WritableRecipeStore.class);
    BlockingRecipeStores stores = new BlockingRecipeStores(underlyingStore);
    ExecutorService testExecutor = Executors.newSingleThreadExecutor();

    @Test
    public void consuming_an_event_should_delegate_to_real_store() {
        stores.consume(consumerRecord());
        verify(underlyingStore).consume(event);
    }

    @Test
    public void after_consuming_123_query_for_100_returns_immediately() {
        stores.consume(consumerRecord());
        RecipeStore blockingStore = stores.blockingStore(0, 100L);

        blockingStore.getRecipeById(Literals.ID);
        verify(underlyingStore).getRecipeById(Literals.ID);
    }

    @Test
    public void after_consuming_123_query_for_124_blocks() throws Exception {
        stores.consume(consumerRecord());
        RecipeStore blockingStore = stores.blockingStore(0, 124L);

        CountDownLatch latch = new CountDownLatch(1);
        testExecutor.submit(() -> {
            blockingStore.getRecipeById(Literals.ID);
            latch.countDown();
        });
        assertThat("latch should never be released", latch.await(100, TimeUnit.MILLISECONDS), is(false));
    }

    @Test
    public void access_to_raw_store_still_available() {
        RecipeStore instance = stores.readable();
        assertThat(instance, is(sameInstance(underlyingStore)));
    }

    private ConsumerRecord<?, Event> consumerRecord() {
        int partition = 0;
        long offset = 123L;
        Object key = null;
        ConsumerRecord<Object, Event> record = new ConsumerRecord<>("topic", partition, offset, key, event);
        return record;
    }
}