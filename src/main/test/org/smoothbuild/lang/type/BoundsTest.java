package org.smoothbuild.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.type.Side.LOWER;
import static org.smoothbuild.lang.type.Side.UPPER;

import org.junit.jupiter.api.Test;

public class BoundsTest {
  @Test
  public void get_lower() {
    var bounds = new Bounds<>(1, 2);
    assertThat(bounds.get(LOWER))
        .isEqualTo(1);
  }

  @Test
  public void get_upper() {
    var bounds = new Bounds<>(1, 2);
    assertThat(bounds.get(UPPER))
        .isEqualTo(2);
  }

  @Test
  public void with_lower() {
    var bounds = new Bounds<>(1, 2);
    assertThat(bounds.with(LOWER, 3))
        .isEqualTo(new Bounds<>(3, 2));
  }

  @Test
  public void with_upper() {
    var bounds = new Bounds<>(1, 2);
    assertThat(bounds.with(UPPER, 3))
        .isEqualTo(new Bounds<>(1, 3));
  }
}
