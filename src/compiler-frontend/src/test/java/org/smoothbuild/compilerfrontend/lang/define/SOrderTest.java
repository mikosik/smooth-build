package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sInt;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sOrder;

import org.junit.jupiter.api.Test;

public class SOrderTest {
  @Test
  public void to_string() {
    var orderS = sOrder(3, sInt(4, 44), sInt(5, 55));
    assertThat(orderS.toString())
        .isEqualTo(
            """
            SOrder(
              evaluationType = [Int]
              elems = [
                SInt(Int, 44, {prj}/build.smooth:4)
                SInt(Int, 55, {prj}/build.smooth:5)
              ]
              location = {prj}/build.smooth:3
            )""");
  }
}
