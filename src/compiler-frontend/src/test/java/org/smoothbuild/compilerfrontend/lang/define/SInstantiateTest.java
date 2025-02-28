package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SInstantiateTest extends FrontendCompilerTestContext {
  @Test
  public void to_source_code() {
    var sSchema = sSchema(list(varA()), varA());
    var id = fqn("my:company:evaluable");
    var sInstantiate = sInstantiate(list(sIntType()), sPolyReference(sSchema, id));
    assertThat(sInstantiate.toSourceCode(set())).isEqualTo("my:company:evaluable<Int>");
  }
}
