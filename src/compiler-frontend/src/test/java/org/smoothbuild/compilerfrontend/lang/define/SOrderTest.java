package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;

public class SOrderTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    var orderS = sOrder(sInt(7), sInt(8));
    assertThat(orderS.toSourceCode()).isEqualTo("[7, 8]");
  }

  @Test
  void to_string() {
    var orderS = sOrder(3, sInt(4, 44), sInt(5, 55));
    assertThat(orderS.toString())
        .isEqualTo(
            """
            SOrder(
              evaluationType = [Int]
              elements = [
                SInt(
                  type = Int
                  bigInteger = 44
                  location = {t-project}/module.smooth:4
                )
                SInt(
                  type = Int
                  bigInteger = 55
                  location = {t-project}/module.smooth:5
                )
              ]
              location = {t-project}/module.smooth:3
            )""");
  }
}
