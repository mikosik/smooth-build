package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.function.Consumer1;

public abstract class AbstractCollectionTestSuite {
  public abstract <E> Collection<E> createCollection(E... elements);

  @Nested
  class _forEach {
    @Test
    void empty_collection() {
      Consumer<Object> consumer1 = mock();
      createCollection().foreach(consumer1::accept);
      verifyNoInteractions(consumer1);
    }

    @Test
    void consumes_elements_in_order() {
      var collected = new ArrayList<Integer>();
      createCollection(1, 2, 3, 4).forEach(collected::add);
      assertThat(collected).containsExactly(1, 2, 3, 4).inOrder();
    }
  }

  @Nested
  class _foreach {
    @Test
    void empty_collection() throws Exception {
      Consumer1<Object, Exception> consumer1 = mock();
      createCollection().foreach(consumer1::accept);
      verifyNoInteractions(consumer1);
    }

    @Test
    void consumes_elements_in_order() {
      var collected = new ArrayList<Integer>();
      createCollection(1, 2, 3, 4).foreach(collected::add);
      assertThat(collected).containsExactly(1, 2, 3, 4).inOrder();
    }

    @Test
    void propagates_exception_from_consumer() {
      var collection = createCollection(1, 2, 3, 4);
      assertCall(() -> collection.foreach(e -> {throw new IOException();})).throwsException(
          IOException.class);
    }
  }

  @Nested
  class _any_matches {
    @Test
    void returns_true_when_one_matches() {
      assertThat(createCollection(1, 2).anyMatches(x -> x.equals(2))).isTrue();
    }

    @Test
    void returns_true_when_all_matches() {
      assertThat(createCollection(2, 2).anyMatches(x -> x.equals(2))).isTrue();
    }

    @Test
    void returns_false_when_none_matches() {
      assertThat(createCollection(2, 2).anyMatches(x -> x.equals(3))).isFalse();
    }

    @Test
    void returns_false_for_empty_collection_even_when_predicate_returns_always_true() {
      assertThat(createCollection().anyMatches(x -> true)).isFalse();
    }
  }
}
