package org.smoothbuild.lang.base.type.api;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestingTypes.ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContextImpl;

public class SideTest extends TestingContextImpl {
  @Nested
  class _reverse_of {
    @Test
    public void upper_is_lower() {
      assertThat(upperST().reversed())
          .isEqualTo(lowerST());
    }

    @Test
    public void lower_is_upper() {
      assertThat(lowerST().reversed())
          .isEqualTo(upperST());
    }
  }

  @Nested
  class _edge_of {
    @Test
    public void lower_is_nothing() {
      assertThat(lowerST().edge())
          .isEqualTo(NOTHING);
    }

    @Test
    public void upper_is_any() {
      assertThat(upperST().edge())
          .isEqualTo(ANY);
    }
  }
}
