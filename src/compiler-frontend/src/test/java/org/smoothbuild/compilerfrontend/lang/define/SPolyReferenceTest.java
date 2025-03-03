package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SPolyReferenceTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    var reference =
        new SPolyReference(sScheme(sIntType()), fqn("my:company:evaluable"), location(7));
    assertThat(reference.toSourceCode()).isEqualTo("my:company:evaluable");
  }

  @Test
  void to_string() {
    var reference = new SPolyReference(sScheme(sIntType()), fqn("referenced"), location(7));
    assertThat(reference.toString())
        .isEqualTo(
            """
            SPolyReference(
              typeScheme = <>Int
              referencedName = referenced
              location = {t-project}/module.smooth:7
            )""");
  }
}
