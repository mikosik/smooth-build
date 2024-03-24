package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sInt;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;

public class SOrderTest {
  @Test
  public void to_string() {
    var orderS = TestingSExpression.sOrder(3, sInt(4, 44), sInt(5, 55));
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
