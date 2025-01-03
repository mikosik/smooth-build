package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class STypeDefinitionTest extends FrontendCompilerTestContext {
  @Test
  void name() {
    var fqn = fqn("my:company:MyStruct");
    var type = sStructType(fqn.parts().getLast().toString(), sIntType(), sBoolType());
    var structTypeDefinition = new STypeDefinition(type, fqn, location());

    assertThat(structTypeDefinition.toSourceCode())
        .isEqualTo(
            """
        MyStruct {
          Int param0,
          Bool param1,
        }""");
  }
}
