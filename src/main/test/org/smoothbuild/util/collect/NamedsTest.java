package org.smoothbuild.util.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.TestContext.location;
import static org.smoothbuild.util.collect.Nameds.toMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.fs.lang.base.Nal;
import org.smoothbuild.compile.fs.lang.base.NalImpl;

public class NamedsTest {
  private static final Nal ONE = new NalImpl("one", location(1));
  private static final Nal TWO = new NalImpl("two", location(2));

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
  }
}
