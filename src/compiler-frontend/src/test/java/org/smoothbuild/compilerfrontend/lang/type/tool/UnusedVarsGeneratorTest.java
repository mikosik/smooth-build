package org.smoothbuild.compilerfrontend.lang.type.tool;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class UnusedVarsGeneratorTest extends FrontendCompilerTestContext {

  @Test
  void test() {
    var generator = new UnusedVarsGenerator(varSetS(varB(), varC()));
    assertThat(List.of(generator.next(), generator.next(), generator.next()))
        .isEqualTo(List.of(varA(), varD(), varE()));
  }
}
