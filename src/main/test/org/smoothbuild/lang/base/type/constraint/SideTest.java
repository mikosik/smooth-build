package org.smoothbuild.lang.base.type.constraint;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestingTypes.ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.constraint.Side.LOWER;
import static org.smoothbuild.lang.base.type.constraint.Side.UPPER;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SideTest {
  @Nested
  class _reverse_of {
    @Test
    public void upper_is_lower() {
      assertThat(UPPER.reversed())
          .isEqualTo(LOWER);
    }

    @Test
    public void lower_is_upper() {
      assertThat(LOWER.reversed())
          .isEqualTo(UPPER);
    }
  }

  @Nested
  class _edge_of {
    @Test
    public void lower_is_nothing() {
      assertThat(LOWER.edge())
          .isEqualTo(NOTHING);
    }

    @Test
    public void upper_is_any() {
      assertThat(UPPER.edge())
          .isEqualTo(ANY);
    }
  }
}
