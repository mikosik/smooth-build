package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.function.Consumer1;

public abstract class AbstractCollectionTestSuite {
  public abstract <E> Collection<E> newCollection(E... elements);

  @Test
  void forEach_empty_collection() {
    Consumer<Object> consumer1 = mock();
    newCollection().foreach(consumer1::accept);
    verifyNoInteractions(consumer1);
  }

  @Test
  void forEach_consumes_elements_in_order() {
    var collected = new ArrayList<Integer>();
    newCollection(1, 2, 3, 4).forEach(collected::add);
    assertThat(collected).containsExactly(1, 2, 3, 4).inOrder();
  }

  @Test
  void foreach_empty_collection() throws Exception {
    Consumer1<Object, Exception> consumer1 = mock();
    newCollection().foreach(consumer1::accept);
    verifyNoInteractions(consumer1);
  }

  @Test
  void foreach_consumes_elements_in_order() {
    var collected = new ArrayList<Integer>();
    newCollection(1, 2, 3, 4).foreach(collected::add);
    assertThat(collected).containsExactly(1, 2, 3, 4).inOrder();
  }

  @Test
  void foreach_propagates_exception_from_consumer() {
    var collection = newCollection(1, 2, 3, 4);
    assertCall(() -> collection.foreach(e -> {
          throw new IOException();
        }))
        .throwsException(IOException.class);
  }

  @Test
  void any_matches_returns_true_when_one_matches() {
    assertThat(newCollection(1, 2).anyMatches(x -> x.equals(2))).isTrue();
  }

  @Test
  void any_matches_returns_true_when_all_matches() {
    assertThat(newCollection(2, 2).anyMatches(x -> x.equals(2))).isTrue();
  }

  @Test
  void any_matches_returns_false_when_none_matches() {
    assertThat(newCollection(2, 2).anyMatches(x -> x.equals(3))).isFalse();
  }

  @Test
  void any_matches_returns_false_for_empty_collection_even_when_predicate_returns_always_true() {
    assertThat(newCollection().anyMatches(x -> true)).isFalse();
  }

  @Test
  void containsAll_empty_collection_contains_empty_collection() {
    var collection = newCollection();
    assertThat(collection.containsAll(newCollection())).isTrue();
  }

  @Test
  void containsAll_empty_collection_does_not_contain_non_empty_collection() {
    var collection = newCollection();
    assertThat(collection.containsAll(newCollection(1))).isFalse();
  }

  @Test
  void containsAll_non_empty_collection_contains_empty_collection() {
    var collection = newCollection(1, 2, 3);
    assertThat(collection.containsAll(newCollection())).isTrue();
  }

  @Test
  void containsAll_non_empty_collection_contains_subset() {
    var collection = newCollection(1, 2, 3);
    assertThat(collection.containsAll(newCollection(1, 2))).isTrue();
  }

  @Test
  void containsAll_non_empty_collection_does_not_contain_non_subset() {
    var collection = newCollection(1, 2, 3);
    assertThat(collection.containsAll(newCollection(1, 4))).isFalse();
  }

  @Test
  void containsAll_non_empty_collection_contains_itself() {
    var collection = newCollection(1, 2, 3);
    assertThat(collection.containsAll(newCollection(1, 2, 3))).isTrue();
  }

  @Test
  void containsAll_non_empty_collection_does_not_contain_superset() {
    var collection = newCollection(1, 2, 3);
    assertThat(collection.containsAll(newCollection(1, 2, 3, 4))).isFalse();
  }
}
