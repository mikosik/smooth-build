package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.smoothbuild.common.collect.Iterables.joinWithCommaToString;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class IterablesTest {
  @Nested
  class _join_with_comma_to_string {
    @Nested
    class _with_func {
      @Test
      public void empty_list(){
        assertThat(joinWithCommaToString(asList(), String::trim))
            .isEqualTo("");
      }

      @Test
      public void one_elem_list(){
        assertThat(joinWithCommaToString(asList(" one "), String::trim))
            .isEqualTo("one");
      }

      @Test
      public void two_elems_list(){
        assertThat(joinWithCommaToString(asList(" one ", " two "), String::trim))
            .isEqualTo("one,two");
      }

      @Test
      public void three_elems_list(){
        assertThat(joinWithCommaToString(asList(" one ", " two ", " three "), String::trim))
            .isEqualTo("one,two,three");
      }
    }

    @Nested
    class _without_func {
      @Test
      public void empty_list(){
        assertThat(joinWithCommaToString(asList()))
            .isEqualTo("");
      }

      @Test
      public void one_elem_list(){
        assertThat(joinWithCommaToString(asList(1)))
            .isEqualTo("1");
      }

      @Test
      public void two_elems_list(){
        assertThat(joinWithCommaToString(asList(1, 2)))
            .isEqualTo("1,2");
      }

      @Test
      public void three_elems_list(){
        assertThat(joinWithCommaToString(asList(1, 2, 3)))
            .isEqualTo("1,2,3");
      }
    }
  }
}
