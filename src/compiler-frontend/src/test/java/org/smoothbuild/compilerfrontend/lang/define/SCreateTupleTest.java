package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;

public class SCreateTupleTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    var sCreateTuple = sCreateTuple(sInt(7), sBlob(8));
    assertThat(sCreateTuple.toSourceCode()).isEqualTo("{7, 0x08}");
  }

  @Test
  void to_string() {
    var sCreateTuple = sCreateTuple(sInt(7), sString("abc"));
    assertThat(sCreateTuple.toString()).isEqualTo("""
        SCreateTuple(
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
