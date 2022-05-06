package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.type.api.Side.LOWER;
import static org.smoothbuild.lang.type.api.Side.UPPER;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.lang.type.api.Side;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.testing.type.TestingTB;

public class TypingBMergeBoundsTest {
  private final TestingTB testingT;

  public TypingBMergeBoundsTest() {
    this.testingT = TestingTB.INSTANCE;
  }

  @Test
  public void var_with_one_lower_bound() {
    var bounds = oneSideBound(LOWER, testingT.string());
    assertThat(bounds.upper()).isEqualTo(testingT.any());
    assertThat(bounds.lower()).isEqualTo(testingT.string());
  }

  @Test
  public void var_with_one_upper_bound() {
    var bounds = oneSideBound(UPPER, testingT.string());
    assertThat(bounds.upper()).isEqualTo(testingT.string());
    assertThat(bounds.lower()).isEqualTo(testingT.nothing());
  }

  @Test
  public void var_with_two_lower_bounds() {
    var bounds = testingT.typing().merge(
        oneSideBound(LOWER, testingT.string()),
        oneSideBound(LOWER, testingT.bool()));
    assertThat(bounds.upper()).isEqualTo(testingT.any());
    assertThat(bounds.lower()).isEqualTo(testingT.any());
  }

  @Test
  public void var_with_two_upper_bounds() {
    var bounds = testingT.typing().merge(
        oneSideBound(UPPER, testingT.string()),
        oneSideBound(UPPER, testingT.bool()));
    assertThat(bounds.upper()).isEqualTo(testingT.nothing());
    assertThat(bounds.lower()).isEqualTo(testingT.nothing());
  }

  public Sides<TypeB> oneSideBound(Side side, TypeB type) {
    return testingT.oneSideBound(side, type);
  }
}
