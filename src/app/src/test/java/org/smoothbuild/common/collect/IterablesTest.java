package org.smoothbuild.common.collect;

import static com.google.common.collect.Streams.stream;
import static com.google.common.truth.Truth.assertThat;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.util.Arrays.asList;
import static org.smoothbuild.common.collect.Iterables.intIterable;
import static org.smoothbuild.common.collect.Iterables.joinWithCommaToString;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class IterablesTest {
  @Nested
  class _int_iterable {
    @Test
    void produces_consecutive_number() {
      var intIterable = intIterable(2);
      assertThat(stream(intIterable).limit(5).toList()).containsExactly(2, 3, 4, 5, 6);
    }

    @Test
    void overflows_after_reaching_max_int() {
      var intIterable = intIterable(MAX_VALUE);
      assertThat(stream(intIterable).limit(3).toList())
          .containsExactly(MAX_VALUE, MIN_VALUE, MIN_VALUE + 1);
    }
  }

  @Nested
  class _join_with_comma_to_string {
    @Nested
    class _with_func {
      @Test
      public void empty_list() {
        assertThat(joinWithCommaToString(asList(), String::trim)).isEqualTo("");
      }

      @Test
      public void one_elem_list() {
        assertThat(joinWithCommaToString(asList(" one "), String::trim)).isEqualTo("one");
      }

      @Test
      public void two_elems_list() {
        assertThat(joinWithCommaToString(asList(" one ", " two "), String::trim))
            .isEqualTo("one,two");
      }

      @Test
      public void three_elems_list() {
        assertThat(joinWithCommaToString(asList(" one ", " two ", " three "), String::trim))
            .isEqualTo("one,two,three");
      }
    }

    @Nested
    class _without_func {
      @Test
      public void empty_list() {
        assertThat(joinWithCommaToString(asList())).isEqualTo("");
      }

      @Test
      public void one_elem_list() {
        assertThat(joinWithCommaToString(asList(1))).isEqualTo("1");
      }

      @Test
      public void two_elems_list() {
        assertThat(joinWithCommaToString(asList(1, 2))).isEqualTo("1,2");
      }

      @Test
      public void three_elems_list() {
        assertThat(joinWithCommaToString(asList(1, 2, 3))).isEqualTo("1,2,3");
      }
    }
  }
}
