package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Maps.map;
import static org.smoothbuild.util.Maps.toMap;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MapsTest {
  @Nested
  class _to_map {
    @Nested
    class _with_value_function {
      @Test
      public void empty_iterable() {
        assertThat(toMap(list(), Object::toString))
            .isEqualTo(Map.of());
      }

      @Test
      public void single_element() {
        assertThat(toMap(list(13), Object::toString))
            .isEqualTo(Map.of(13, "13"));
      }

      @Test
      public void many_elements() {
        assertThat(toMap(list(1, 2, 3), Object::toString))
            .isEqualTo(Map.of(1, "1", 2, "2", 3, "3"));
      }

      @Test
      public void duplicate_keys() {
        assertCall(() -> toMap(list(1, 1), Object::toString))
            .throwsException(IllegalArgumentException.class);
      }

      @Test
      public void null_element() {
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
    class _with_key_and_value_function {
      @Test
      public void empty_iterable() {
        assertThat(toMap(list(), Object::toString, Object::toString))
            .isEqualTo(Map.of());
      }

      @Test
      public void single_element() {
        assertThat(toMap(list(5), Object::toString, i -> i * i))
            .isEqualTo(Map.of("5", 25));
      }

      @Test
      public void many_elements() {
        assertThat(toMap(list(2, 3, 4), Object::toString, i -> i * i))
            .isEqualTo(Map.of("2", 4, "3", 9, "4", 16));
      }

      @Test
      public void duplicate_keys() {
        assertCall(() -> toMap(list(1,2), k -> "key", Object::toString))
            .throwsException(IllegalArgumentException.class);
      }

      @Test
      public void null_element() {
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
    public void many_elements() {
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
}
