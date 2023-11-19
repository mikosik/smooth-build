package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.entry;
import static org.smoothbuild.common.collect.Lists.list;
import static org.smoothbuild.common.collect.Maps.computeIfAbsent;
import static org.smoothbuild.common.collect.Maps.mapEntries;
import static org.smoothbuild.common.collect.Maps.override;
import static org.smoothbuild.common.collect.Maps.sort;
import static org.smoothbuild.common.collect.Maps.toMap;
import static org.smoothbuild.common.collect.Maps.zip;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MapsTest {
  @Nested
  class _to_map {
    @Nested
    class _with_value_func {
      @Test
      public void empty_iterable() {
        assertThat(toMap(list(), Object::toString)).isEqualTo(Map.of());
      }

      @Test
      public void single_elem() {
        assertThat(toMap(list(13), Object::toString)).isEqualTo(Map.of(13, "13"));
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
        assertCall(() -> toMap(list(13), e -> null)).throwsException(NullPointerException.class);
      }
    }

    @Nested
    class _with_key_and_value_func {
      @Test
      public void empty_iterable() {
        assertThat(toMap(list(), Object::toString, Object::toString)).isEqualTo(Map.of());
      }

      @Test
      public void single_elem() {
        assertThat(toMap(list(5), Object::toString, i -> i * i)).isEqualTo(Map.of("5", 25));
      }

      @Test
      public void many_elems() {
        assertThat(toMap(list(2, 3, 4), Object::toString, i -> i * i))
            .isEqualTo(Map.of("2", 4, "3", 9, "4", 16));
      }

      @Test
      public void duplicate_keys() {
        assertCall(() -> toMap(list(1, 2), k -> "key", Object::toString))
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
  class _override {
    @Test
    public void two_empty_maps() {
      assertThat(Maps.<Integer, String>override(ImmutableMap.of(), ImmutableMap.of()))
          .isEqualTo(Map.of());
    }

    @Test
    public void empty_and_non_empty() {
      assertThat(override(ImmutableMap.of(), ImmutableMap.of(1, "one")))
          .isEqualTo(Map.of(1, "one"));
    }

    @Test
    public void non_empty_and_empty() {
      assertThat(override(ImmutableMap.of(1, "one"), ImmutableMap.of()))
          .isEqualTo(Map.of(1, "one"));
    }

    @Test
    public void non_empty_and_non_empty() {
      assertThat(override(ImmutableMap.of(1, "one"), ImmutableMap.of(2, "two")))
          .isEqualTo(Map.of(1, "one", 2, "two"));
    }

    @Test
    public void with_same_mapping() {
      assertThat(override(ImmutableMap.of(1, "first map"), ImmutableMap.of(1, "second map")))
          .isEqualTo(Map.of(1, "first map"));
    }
  }

  @Nested
  class _map_entries_separate_functions {
    @Test
    public void empty_map() {
      assertThat(mapEntries(Map.of(), Object::toString, Object::toString)).isEqualTo(Map.of());
    }

    @Test
    public void many_elems() {
      assertThat(mapEntries(Map.of(2, "2", 3, "3"), i -> i * i, v -> v + "^2"))
          .isEqualTo(Map.of(4, "2^2", 9, "3^2"));
    }

    @Test
    public void duplicate_keys() {
      assertCall(() -> mapEntries(Map.of(1, "1", 2, "2"), k -> "key", Object::toString))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void null_key() {
      assertCall(() -> mapEntries(Map.of(2, "2"), e -> null, Object::toString))
          .throwsException(NullPointerException.class);
    }

    @Test
    public void null_value() {
      assertCall(() -> mapEntries(Map.of(2, "2"), Object::toString, e -> null))
          .throwsException(NullPointerException.class);
    }
  }

  @Nested
  class _map_entries_single_function {
    @Test
    public void empty_map() {
      assertThat(mapEntries(
              Map.of(), e -> entry(e.getKey().toString(), e.getValue().toString())))
          .isEqualTo(Map.of());
    }

    @Test
    public void many_elems() {
      var mapped = mapEntries(
          Map.of(2, "2", 3, "3"), e -> entry(e.getKey() * e.getKey(), e.getValue() + "^2"));
      assertThat(mapped).isEqualTo(Map.of(4, "2^2", 9, "3^2"));
    }

    @Test
    public void duplicate_keys() {
      assertCall(() -> mapEntries(Map.of(2, "2", 3, "3"), e -> entry("key", e.getValue())))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void null_key() {
      assertCall(() -> mapEntries(Map.of(2, "2"), e -> entry(null, e.getValue())))
          .throwsException(NullPointerException.class);
    }

    @Test
    public void null_value() {
      assertCall(() -> mapEntries(Map.of(2, "2"), e -> entry(e.getKey(), null)))
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
      assertThat(result).isEqualTo("three");
    }

    @Test
    public void when_map_does_not_contain_key_then_mapping_func_is_invoked() {
      var map = new HashMap<Integer, String>();
      String result = computeIfAbsent(map, 3, Object::toString);
      assertThat(result).isEqualTo("3");
    }

    @Test
    public void when_map_does_not_contain_key_then_computed_value_is_added_to_map() {
      var map = new HashMap<Integer, String>();
      computeIfAbsent(map, 3, Object::toString);
      assertThat(map.get(3)).isEqualTo("3");
    }

    @Test
    public void mapping_func_can_modify_map() {
      var map = new HashMap<Integer, String>();
      map.put(1, "1");
      computeIfAbsent(map, 3, integer -> {
        map.clear();
        return integer.toString();
      });
      assertThat(map).containsExactly(3, "3");
    }

    @Test
    public void exception_is_propagated() {
      var map = new HashMap<Integer, String>();
      map.put(3, "three");
      assertCall(() -> computeIfAbsent(map, 1, i -> {
            throw new IOException();
          }))
          .throwsException(IOException.class);
    }
  }

  @Nested
  class _sort {
    @Test
    public void empty_map() {
      assertThat(sort(Map.of(), (v1, v2) -> 0)).isEqualTo(Map.of());
    }

    @Test
    public void one_entry_map() {
      assertThat(sort(Map.of(1, "one"), comparingByKey())).isEqualTo(Map.of(1, "one"));
    }

    @Test
    public void many_entry_map() {
      assertThat(sort(Map.of(3, "three", 2, "two", 1, "one"), comparingByKey()))
          .isEqualTo(ImmutableMap.of(1, "one", 2, "two", 3, "three"));
    }
  }

  @Nested
  class _zip {
    @Test
    public void empty_map() {
      assertThat(zip(list(), list())).isEqualTo(Map.of());
    }

    @Test
    public void one_entry_map() {
      assertThat(zip(list(1), list("one"))).isEqualTo(Map.of(1, "one"));
    }

    @Test
    public void many_entry_map() {
      assertThat(zip(list(1, 2, 3), list("one", "two", "three")))
          .isEqualTo(Map.of(1, "one", 2, "two", 3, "three"));
    }

    @Test
    public void different_size_of_keys_and_values_causes_exc() {
      assertCall(() -> zip(list(1, 2, 3), list("one", "two")))
          .throwsException(new IllegalArgumentException("List sizes differ 3 != 2."));
    }
  }
}
