package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class OrderSTest extends TestContext {
  @Test
  public void to_string() {
    var orderS = orderS(3, intS(4, 44), intS(5, 55));
    assertThat(orderS.toString())
        .isEqualTo("""
            OrderS(
              evalT = [Int]
              elems = [
                IntS(Int, 44, myBuild.smooth:4)
                IntS(Int, 55, myBuild.smooth:5)
              ]
              loc = myBuild.smooth:3
            )""");
  }
}