package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Labeled.labeled;
import static org.smoothbuild.util.collect.Nameables.toMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class NameablesTest {

  private static final Labeled<Integer> TWO = labeled("two", 2);
  private static final Labeled<Integer> ONE = labeled("one", 1);

  @Nested
  class _to_map {
    @Test
    public void empty_collection() {
      assertThat(toMap(List.of()))
          .isEqualTo(new HashMap<>());
    }

    @Test
    public void non_empty_collection() {
      assertThat(toMap(List.of(ONE, TWO)))
          .isEqualTo(Map.of("one", ONE, "two", TWO));
    }

    @Test
    public void collection_with_nameables_without_name() {
      assertThat(toMap(List.of(ONE, labeled(11), TWO)))
          .isEqualTo(Map.of("one", ONE, "two", TWO));
    }
  }
}
