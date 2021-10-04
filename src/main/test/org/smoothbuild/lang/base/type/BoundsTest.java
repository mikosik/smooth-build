package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.Bounds.oneSideBound;
import static org.smoothbuild.lang.base.type.TestingTypes.ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.Types.lower;
import static org.smoothbuild.lang.base.type.Types.upper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BoundsTest {
  @Test
  public void variable_with_one_lower_bound() {
    var bounds = oneSideBound(lower(), STRING);
    assertThat(bounds.get(lower()))
        .isEqualTo(STRING);
  }

  @Test
  public void variable_with_one_upper_bound() {
    var bounds = oneSideBound(upper(), STRING);
    assertThat(bounds.get(upper()))
        .isEqualTo(STRING);
  }

  @Test
  public void variable_with_two_lower_bounds() {
    var bounds = oneSideBound(lower(), STRING);
    bounds = bounds.mergeWith(oneSideBound(lower(), BOOL));
    assertThat(bounds.get(lower()))
        .isEqualTo(ANY);
  }

  @Test
  public void variable_with_two_upper_bounds() {
    var bounds = oneSideBound(upper(), STRING);
    bounds = bounds.mergeWith(oneSideBound(upper(), BOOL));
    assertThat(bounds.get(upper()))
        .isEqualTo(NOTHING);
  }

  @Nested
  class _is_consistent {
    @Nested
    class _returns_true {
      @Test
      public void when_lower_bound_is_set() {
        var bounds = oneSideBound(lower(), STRING);
        assertThat(bounds.areConsistent())
            .isTrue();
      }

      @Test
      public void when_upper_bound_is_set() {
        var bounds = oneSideBound(upper(), STRING);
        assertThat(bounds.areConsistent())
            .isTrue();
      }

      @Test
      public void when_lower_and_upper_bound_are_equal() {
        var bounds = oneSideBound(upper(), STRING);
        bounds = bounds.mergeWith(oneSideBound(lower(), STRING));
        assertThat(bounds.areConsistent())
            .isTrue();
      }
    }

    @Nested
    class _returns_false {
      @Test
      public void when_lower_bound_is_greater_than_upper_bound() {
        var bounds = oneSideBound(upper(), NOTHING);
        bounds = bounds.mergeWith(oneSideBound(lower(), ANY));
        assertThat(bounds.areConsistent())
            .isFalse();
      }

      @Test
      public void when_lower_bound_is_not_comparable_with_upper_bound() {
        var bounds = oneSideBound(upper(), STRING);
        bounds = bounds.mergeWith(oneSideBound(lower(), BOOL));
        assertThat(bounds.areConsistent())
            .isFalse();
      }
    }
  }
}
