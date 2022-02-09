package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.testing.type.TestingT;

public abstract class TypingMergeBoundsTestCase<T extends Type> {
  private final TestingT<T> testingT;

  public TypingMergeBoundsTestCase(TestingT<T> testingT) {
    this.testingT = testingT;
  }

  @Test
  public void var_with_one_lower_bound() {
    var bounds = oneSideBound(lower(), testingT.string());
    assertThat(bounds.upper()).isEqualTo(testingT.any());
    assertThat(bounds.lower()).isEqualTo(testingT.string());
  }

  @Test
  public void var_with_one_upper_bound() {
    var bounds = oneSideBound(upper(), testingT.string());
    assertThat(bounds.upper()).isEqualTo(testingT.string());
    assertThat(bounds.lower()).isEqualTo(testingT.nothing());
  }

  @Test
  public void var_with_two_lower_bounds() {
    var bounds = testingT.typing().merge(
        oneSideBound(lower(), testingT.string()),
        oneSideBound(lower(), testingT.bool()));
    assertThat(bounds.upper()).isEqualTo(testingT.any());
    assertThat(bounds.lower()).isEqualTo(testingT.any());
  }

  @Test
  public void var_with_two_upper_bounds() {
    var bounds = testingT.typing().merge(
        oneSideBound(upper(), testingT.string()),
        oneSideBound(upper(), testingT.bool()));
    assertThat(bounds.upper()).isEqualTo(testingT.nothing());
    assertThat(bounds.lower()).isEqualTo(testingT.nothing());
  }

  private Side<T> lower() {
    return testingT.lower();
  }

  private Side<T> upper() {
    return testingT.upper();
  }

  public Bounds<T> oneSideBound(Side<T> side, T type) {
    return testingT.oneSideBound(side, type);
  }
}
