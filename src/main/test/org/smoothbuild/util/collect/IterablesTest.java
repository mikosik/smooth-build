package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.smoothbuild.util.collect.Iterables.toCommaSeparatedString;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class IterablesTest {
  @Nested
  class _to_comma_separated_string {
    @Nested
    class _with_func {
      @Test
      public void empty_list(){
        assertThat(toCommaSeparatedString(asList(), String::trim))
            .isEqualTo("");
      }

      @Test
      public void one_elem_list(){
        assertThat(toCommaSeparatedString(asList(" one "), String::trim))
            .isEqualTo("one");
      }

      @Test
      public void two_elems_list(){
        assertThat(toCommaSeparatedString(asList(" one ", " two "), String::trim))
            .isEqualTo("one,two");
      }

      @Test
      public void three_elems_list(){
        assertThat(toCommaSeparatedString(asList(" one ", " two ", " three "), String::trim))
            .isEqualTo("one,two,three");
      }
    }

    @Nested
    class _without_func {
      @Test
      public void empty_list(){
        assertThat(toCommaSeparatedString(asList()))
            .isEqualTo("");
      }

      @Test
      public void one_elem_list(){
        assertThat(toCommaSeparatedString(asList(1)))
            .isEqualTo("1");
      }

      @Test
      public void two_elems_list(){
        assertThat(toCommaSeparatedString(asList(1, 2)))
            .isEqualTo("1,2");
      }

      @Test
      public void three_elems_list(){
        assertThat(toCommaSeparatedString(asList(1, 2, 3)))
            .isEqualTo("1,2,3");
      }
    }
  }
}
