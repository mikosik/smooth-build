package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Maps.computeIfAbsent;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import java.util.HashMap;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MapsTest {
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
}
