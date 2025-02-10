package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SCombineTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    var sCombine = sCombine(sInt(7), sBlob(8));
    assertThat(sCombine.toSourceCode(varSetS())).isEqualTo("{7, 0x08}");
  }

  @Test
  void to_string() {
    var sCombine = sCombine(sInt(7), sString("abc"));
    assertThat(sCombine.toString())
        .isEqualTo(
            """
        SCombine(
          evaluationType = {Int,String}
          elements = [
            SInt(
              type = Int
              bigInteger = 7
              location = {t-project}/module.smooth:1
            )
            SString(
              type = String
              string = abc
              location = {t-project}/module.smooth:1
            )
          ]
          location = {t-project}/module.smooth:13
        )""");
  }
}
