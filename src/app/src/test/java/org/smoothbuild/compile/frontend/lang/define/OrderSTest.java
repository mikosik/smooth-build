package org.smoothbuild.compile.frontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class OrderSTest extends TestContext {
  @Test
  public void to_string() {
    var orderS = orderS(3, intS(4, 44), intS(5, 55));
    assertThat(orderS.toString())
        .isEqualTo(
            """
            OrderS(
              evaluationT = [Int]
              elems = [
                IntS(Int, 44, build.smooth:4)
                IntS(Int, 55, build.smooth:5)
              ]
              location = build.smooth:3
            )""");
  }
}
