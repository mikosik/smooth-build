package org.smoothbuild.util.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.type.Side.LOWER;
import static org.smoothbuild.util.type.Side.UPPER;

import org.junit.jupiter.api.Test;

public class SidesTest {
  @Test
  public void get_lower() {
    var sides = new Sides<>(1, 2);
    assertThat(sides.get(LOWER))
        .isEqualTo(1);
  }

  @Test
  public void get_upper() {
    var sides = new Sides<>(1, 2);
    assertThat(sides.get(UPPER))
        .isEqualTo(2);
  }

  @Test
  public void with_lower() {
    var sides = new Sides<>(1, 2);
    assertThat(sides.with(LOWER, 3))
        .isEqualTo(new Sides<>(3, 2));
  }

  @Test
  public void with_upper() {
    var sides = new Sides<>(1, 2);
    assertThat(sides.with(UPPER, 3))
        .isEqualTo(new Sides<>(1, 3));
  }
}
