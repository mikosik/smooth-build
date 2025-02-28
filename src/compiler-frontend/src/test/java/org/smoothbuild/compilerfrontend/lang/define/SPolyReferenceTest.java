package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SPolyReferenceTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    var refS = new SPolyReference(sSchema(sIntType()), fqn("my:company:evaluable"), location(7));
    assertThat(refS.toSourceCode()).isEqualTo("my:company:evaluable");
  }

  @Test
  void to_string() {
    var refS = new SPolyReference(sSchema(sIntType()), fqn("referenced"), location(7));
    assertThat(refS.toString())
        .isEqualTo(
            """
            SPolyReference(
              schema = <>Int
              referencedName = referenced
              location = {t-project}/module.smooth:7
            )""");
  }
}
