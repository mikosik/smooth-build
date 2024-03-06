package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.orderS;

import org.junit.jupiter.api.Test;

public class OrderSTest {
  @Test
  public void to_string() {
    var orderS = orderS(3, intS(4, 44), intS(5, 55));
    assertThat(orderS.toString())
        .isEqualTo(
            """
            OrderS(
              evaluationType = [Int]
              elems = [
                IntS(Int, 44, {prj}/build.smooth:4)
                IntS(Int, 55, {prj}/build.smooth:5)
              ]
              location = {prj}/build.smooth:3
            )""");
  }
}
