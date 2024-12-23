package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SConstructorTest extends FrontendCompilerTestContext {
  @Test
  void to_string() {
    var structTS = sStructType("MyStruct", nlist(sSig(sIntType(), "field")));
    var constructorS = sConstructor(17, structTS, "constructorName");
    assertThat(constructorS.toString())
        .isEqualTo(
            """
            SConstructor(
              name = constructorName
              schema = <>(Int)->MyStruct
              params = [
                SItem(
                  type = Int
                  name = field
                  defaultValueId = None
                  location = {t-project}/module.smooth:2
                )
              ]
              location = {t-project}/module.smooth:17
            )""");
  }
}
