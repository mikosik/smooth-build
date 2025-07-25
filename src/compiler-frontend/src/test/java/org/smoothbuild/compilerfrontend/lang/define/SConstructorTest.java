package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;

public class SConstructorTest extends FrontendCompilerTestContext {
  @Test
  void to_string() {
    var structTS = sStructType("MyStruct", nlist(sSig(sIntType(), "field")));
    var constructorS = sConstructor(17, structTS, "constructorName");
    assertThat(constructorS.toString())
        .isEqualTo(
            """
            SConstructor(
              fqn = constructorName
              type = (Int)->MyStruct
              params = [
                SItem(
                  type = Int
                  fqn = MyStruct:field
                  defaultValue = None
                  location = {t-project}/module.smooth:2
                )
              ]
              location = {t-project}/module.smooth:17
            )""");
  }
}
