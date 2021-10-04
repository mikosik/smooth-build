package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestingTypes.ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.Types.lower;
import static org.smoothbuild.lang.base.type.Types.upper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SideTest {
  @Nested
  class _reverse_of {
    @Test
    public void upper_is_lower() {
      assertThat(upper().reversed())
          .isEqualTo(lower());
    }

    @Test
    public void lower_is_upper() {
      assertThat(lower().reversed())
          .isEqualTo(upper());
    }
  }

  @Nested
  class _edge_of {
    @Test
    public void lower_is_nothing() {
      assertThat(lower().edge())
          .isEqualTo(NOTHING);
    }

    @Test
    public void upper_is_any() {
      assertThat(upper().edge())
          .isEqualTo(ANY);
    }
  }
}
