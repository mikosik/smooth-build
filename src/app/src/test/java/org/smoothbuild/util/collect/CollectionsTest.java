package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CollectionsTest {
  @Nested
  class toMap {
    @Test
    public void non_empty_collection() {
      assertThat(Collections.toMap(list("abc", "defg", "hijkl"), String::length))
          .containsExactly(3, "abc", 4, "defg", 5, "hijkl");
    }

    @Test
    public void empty_collection() {
      assertThat(Collections.toMap(list(), String::length))
          .isEmpty();
    }
  }

  @Nested
  class toMapWithValueMapper {
    @Test
    public void non_empty_collection() {
      assertThat(Collections.toMap(list("abc", "defg", "hijkl"), String::toUpperCase, String::length))
          .containsExactly("ABC", 3, "DEFG", 4, "HIJKL", 5);
    }

    @Test
    public void value_mapper_allows_null_values() {
      List<String> collection = list("abc", "defg", "hijkl");
      assertThat(Collections.toMap(collection, x -> x, x -> null))
          .containsExactly("abc", null, "defg", null, "hijkl", null);
    }

    @Test
    public void empty_collection() {
      assertThat(Collections.toMap(list(), String::toUpperCase, String::length))
          .isEmpty();
    }
  }
}
