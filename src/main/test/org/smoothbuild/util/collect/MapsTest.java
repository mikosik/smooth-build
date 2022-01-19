package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.Map.Entry.comparingByKey;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Maps.computeIfAbsent;
import static org.smoothbuild.util.collect.Maps.map;
import static org.smoothbuild.util.collect.Maps.sort;
import static org.smoothbuild.util.collect.Maps.toMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

public class MapsTest {
  @Nested
  class _to_map {
    @Nested
    class _with_value_func {
      @Test
      public void empty_iterable() {
        assertThat(toMap(list(), Object::toString))
            .isEqualTo(Map.of());
      }

      @Test
      public void single_elem() {
        assertThat(toMap(list(13), Object::toString))
            .isEqualTo(Map.of(13, "13"));
      }

      @Test
      public void many_elems() {
        assertThat(toMap(list(1, 2, 3), Object::toString))
            .isEqualTo(Map.of(1, "1", 2, "2", 3, "3"));
      }

      @Test
      public void duplicate_keys() {
        assertCall(() -> toMap(list(1, 1), Object::toString))
            .throwsException(IllegalArgumentException.class);
      }

      @Test
      public void null_elem() {
        List<Object> iterableWithNull = asList(null, null);
        assertCall(() -> toMap(iterableWithNull, Object::toString))
            .throwsException(NullPointerException.class);
      }

      @Test
      public void null_value() {
        assertCall(() -> toMap(list(13), e -> null))
            .throwsException(NullPointerException.class);
      }
    }

    @Nested
    class _with_key_and_value_func {
      @Test
      public void empty_iterable() {
        assertThat(toMap(list(), Object::toString, Object::toString))
            .isEqualTo(Map.of());
      }

      @Test
      public void single_elem() {
        assertThat(toMap(list(5), Object::toString, i -> i * i))
            .isEqualTo(Map.of("5", 25));
      }

      @Test
      public void many_elems() {
        assertThat(toMap(list(2, 3, 4), Object::toString, i -> i * i))
            .isEqualTo(Map.of("2", 4, "3", 9, "4", 16));
      }

      @Test
      public void duplicate_keys() {
        assertCall(() -> toMap(list(1,2), k -> "key", Object::toString))
            .throwsException(IllegalArgumentException.class);
      }

      @Test
      public void null_elem() {
        List<Object> iterableWithNull = asList(null, null);
        assertCall(() -> toMap(iterableWithNull, Object::toString, Object::toString))
            .throwsException(NullPointerException.class);
      }

      @Test
      public void null_key() {
        assertCall(() -> toMap(list(13), e -> null, Object::toString))
            .throwsException(NullPointerException.class);
      }

      @Test
      public void null_value() {
        assertCall(() -> toMap(list(13), Object::toString, e -> null))
            .throwsException(NullPointerException.class);
      }
    }
  }

  @Nested
  class _map {
    @Test
    public void empty_map() {
      assertThat(map(Map.of(), Object::toString, Object::toString))
          .isEqualTo(Map.of());
    }

    @Test
    public void many_elems() {
      assertThat(map(Map.of(2, "2", 3, "3"), i -> i * i, v -> v + "^2"))
          .isEqualTo(Map.of(4, "2^2", 9, "3^2"));
    }

    @Test
    public void duplicate_keys() {
      assertCall(() -> map(Map.of(1, "1", 2, "2"), k -> "key", Object::toString))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void null_key() {
      assertCall(() -> map(Map.of(2, "2"), e -> null, Object::toString))
          .throwsException(NullPointerException.class);
    }

    @Test
    public void null_value() {
      assertCall(() -> map(Map.of(2, "2"), Object::toString, e -> null))
          .throwsException(NullPointerException.class);
    }
  }
  @Nested
  class _compute_if_absent {
    @Test
    public void when_map_contains_key_then_it_is_returned() {
      var map = new HashMap<Integer, String>();
      map.put(3, "three");
      String result = computeIfAbsent(map, 3, Object::toString);
      assertThat(result)
          .isEqualTo("three");
    }

    @Test
    public void when_map_does_not_contain_key_then_mapping_func_is_invoked() {
      var map = new HashMap<Integer, String>();
      String result = computeIfAbsent(map, 3, Object::toString);
      assertThat(result)
          .isEqualTo("3");
    }

    @Test
    public void when_map_does_not_contain_key_then_computed_value_is_added_to_map() {
      var map = new HashMap<Integer, String>();
      computeIfAbsent(map, 3, Object::toString);
      assertThat(map.get(3))
          .isEqualTo("3");
    }

    @Test
    public void mapping_func_can_modify_map() {
      var map = new HashMap<Integer, String>();
      map.put(1, "1");
      computeIfAbsent(map, 3, integer -> {
        map.clear();
        return integer.toString();
      });
      assertThat(map)
          .containsExactly(3, "3");
    }
  }

  @Nested
  class _sort {
    @Test
    public void empty_map() {
      assertThat(sort(Map.of(), (v1, v2) -> 0))
          .isEqualTo(Map.of());
    }

    @Test
    public void one_entry_map() {
      assertThat(sort(Map.of(1, "one"), comparingByKey()))
          .isEqualTo(Map.of(1, "one"));
    }

    @Test
    public void many_entry_map() {
      assertThat(sort(Map.of(3, "three", 2, "two", 1, "one"), comparingByKey()))
          .isEqualTo(ImmutableMap.of(1, "one", 2, "two", 3, "three"));
    }
  }
}
