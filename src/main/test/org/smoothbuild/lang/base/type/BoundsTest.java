package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.Bounds.oneSideBound;
import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.lang.base.type.Side.UPPER;
import static org.smoothbuild.lang.base.type.TestingTypes.ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BoundsTest {
  @Test
  public void variable_with_one_lower_bound() {
    var bounds = oneSideBound(LOWER, STRING);
    assertThat(bounds.get(LOWER))
        .isEqualTo(STRING);
  }

  @Test
  public void variable_with_one_upper_bound() {
    var bounds = oneSideBound(UPPER, STRING);
    assertThat(bounds.get(UPPER))
        .isEqualTo(STRING);
  }

  @Test
  public void variable_with_two_lower_bounds() {
    var bounds = oneSideBound(LOWER, STRING);
    bounds = bounds.mergeWith(oneSideBound(LOWER, BOOL));
    assertThat(bounds.get(LOWER))
        .isEqualTo(ANY);
  }

  @Test
  public void variable_with_two_upper_bounds() {
    var bounds = oneSideBound(UPPER, STRING);
    bounds = bounds.mergeWith(oneSideBound(UPPER, BOOL));
    assertThat(bounds.get(UPPER))
        .isEqualTo(NOTHING);
  }

  @Nested
  class _is_consistent {
    @Nested
    class _returns_true {
      @Test
      public void when_lower_bound_is_set() {
        var bounds = oneSideBound(LOWER, STRING);
        assertThat(bounds.areConsistent())
            .isTrue();
      }

      @Test
      public void when_upper_bound_is_set() {
        var bounds = oneSideBound(UPPER, STRING);
        assertThat(bounds.areConsistent())
            .isTrue();
      }

      @Test
      public void when_lower_and_upper_bound_are_equal() {
        var bounds = oneSideBound(UPPER, STRING);
        bounds = bounds.mergeWith(oneSideBound(LOWER, STRING));
        assertThat(bounds.areConsistent())
            .isTrue();
      }
    }

    @Nested
    class _returns_false {
      @Test
      public void when_lower_bound_is_greater_than_upper_bound() {
        var bounds = oneSideBound(UPPER, NOTHING);
        bounds = bounds.mergeWith(oneSideBound(LOWER, ANY));
        assertThat(bounds.areConsistent())
            .isFalse();
      }

      @Test
      public void when_lower_bound_is_not_comparable_with_upper_bound() {
        var bounds = oneSideBound(UPPER, STRING);
        bounds = bounds.mergeWith(oneSideBound(LOWER, BOOL));
        assertThat(bounds.areConsistent())
            .isFalse();
      }
    }
  }
}
