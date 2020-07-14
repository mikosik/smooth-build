package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.smoothbuild.util.Iterables.map;

import java.util.ArrayList;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class IterablesTest {
  @Nested
  class map {
    @Test
    public void returns_empty_iterable_for_empty_argument(){
      assertThat(map(new ArrayList<String>(), String::toUpperCase))
          .isEmpty();
    }

    @Test
    public void returns_mapped_one_element(){
      assertThat(map(asList("abc"), String::toUpperCase))
          .containsExactly("ABC");
    }

    @Test
    public void mapping_with_two_elements(){
      assertThat(map(asList("abc", "def"), String::toUpperCase))
          .containsExactly("ABC", "DEF");
    }
  }
}
