package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestingTypes.ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class TypeFactoryTest extends TestingContext {
  @Nested
  class _merge_bounds {
    @Test
    public void variable_with_one_lower_bound() {
      var bounds = typeFactory().oneSideBound(lower(), STRING);
      assertThat(bounds.upper()).isEqualTo(ANY);
      assertThat(bounds.lower()).isEqualTo(STRING);
    }

    @Test
    public void variable_with_one_upper_bound() {
      var bounds = typeFactory().oneSideBound(upper(), STRING);
      assertThat(bounds.upper()).isEqualTo(STRING);
      assertThat(bounds.lower()).isEqualTo(NOTHING);
    }

    @Test
    public void variable_with_two_lower_bounds() {
      var bounds = typeFactory().merge(
          typeFactory().oneSideBound(lower(), STRING),
          typeFactory().oneSideBound(lower(), BOOL));
      assertThat(bounds.upper()).isEqualTo(ANY);
      assertThat(bounds.lower()).isEqualTo(ANY);
    }

    @Test
    public void variable_with_two_upper_bounds() {
      var bounds = typeFactory().merge(
          typeFactory().oneSideBound(upper(), STRING),
          typeFactory().oneSideBound(upper(), BOOL));
      assertThat(bounds.upper()).isEqualTo(NOTHING);
      assertThat(bounds.lower()).isEqualTo(NOTHING);
    }
  }
}
