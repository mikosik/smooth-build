package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;

public class SInstantiateTest extends FrontendCompilerTestContext {
  @Test
  public void to_source_code() {
    var sScheme = sScheme(list(varA()), varA());
    var id = fqn("my:company:evaluable");
    var sInstantiate = sInstantiate(list(sIntType()), sPolyReference(sScheme, id));
    assertThat(sInstantiate.toSourceCode()).isEqualTo("my:company:evaluable<Int>");
  }
}
