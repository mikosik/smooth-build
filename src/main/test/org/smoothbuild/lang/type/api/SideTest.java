package org.smoothbuild.lang.type.api;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.type.api.Side.LOWER;
import static org.smoothbuild.lang.type.api.Side.UPPER;

import org.junit.jupiter.api.Test;

public class SideTest {
  @Test
  public void lower_of() {
    assertThat(LOWER.of("lower", "upper"))
        .isEqualTo("lower");
  }

  @Test
  public void upper_of() {
    assertThat(UPPER.of("lower", "upper"))
        .isEqualTo("upper");
  }
}
