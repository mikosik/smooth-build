package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SModuleTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    var sStructType = sStructType("MyStruct", sIntType());
    var structDefinition = new STypeDefinition(sStructType, fqn("module:MyStruct"), location());

    var scope = new SScope(bindings(structDefinition), bindings(idSFunc()));
    assertThat(new SModule(scope, null).toSourceCode())
        .isEqualTo(
            """
            MyStruct {
              Int param0,
            }
            A myId<A>(A a)
              = a<>;
            """);
  }
}
