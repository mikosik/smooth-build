package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.compilerfrontend.lang.name.Bindings.bindings;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;

public class SModuleTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    var sStructType = sStructType("MyStruct", sIntType());
    var structDefinition = new STypeDefinition(sStructType, fqn("module:MyStruct"), location());
    var func = idSFunc().name();
    var struct = structDefinition.name();
    var scope = new SScope(bindings(), bindings());
    var types = map(struct, structDefinition);
    var evaluables = map(func, idSFunc());
    assertThat(new SModule(types, evaluables, scope).toSourceCode())
        .isEqualTo(
            """
            MyStruct {
              Int param0,
            }
            A myId<A>(A a)
              = a;
            """);
  }
}
