package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SMonoReferenceTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    var refS = new SMonoReference(sSchema(sIntType()), fqn("my:company:evaluable"), location(7));
    assertThat(refS.toSourceCode()).isEqualTo("my:company:evaluable");
  }

  @Test
  void to_string() {
    var refS = new SMonoReference(sSchema(sIntType()), fqn("referenced"), location(7));
    assertThat(refS.toString())
        .isEqualTo(
            """
            SMonoReference(
              schema = <>Int
              referencedName = referenced
              location = {t-project}/module.smooth:7
            )""");
  }
}
