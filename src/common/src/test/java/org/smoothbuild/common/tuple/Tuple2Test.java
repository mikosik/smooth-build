package org.smoothbuild.common.tuple;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class Tuple2Test {
  @Nested
  class _map {
    @Nested
    class _element1 {
      @Test
      void returns_tuple_with_mapped_element() {
        var tuple = new Tuple2<>("a", 1);
        assertThat(tuple.map1(String::toUpperCase)).isEqualTo(new Tuple2<>("A", 1));
      }

      @Test
      void propagates_exception_from_mapper() {
        var tuple = new Tuple2<>("a", 1);
        var exception = new Exception("message");
        assertCall(() -> tuple.map1(e -> {
              throw exception;
            }))
            .throwsException(exception);
      }
    }

    @Nested
    class _element2 {
      @Test
      void returns_tuple_with_mapped_element() {
        var tuple = new Tuple2<>("a", 1);
        assertThat(tuple.map2(e -> e + 7)).isEqualTo(new Tuple2<>("a", 8));
      }

      @Test
      void propagates_exception_from_mapper() {
        var tuple = new Tuple2<>("a", 1);
        var exception = new Exception("message");
        assertCall(() -> tuple.map2(e -> {
              throw exception;
            }))
            .throwsException(exception);
      }
    }
  }
}
