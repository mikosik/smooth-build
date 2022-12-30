package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class ParamRefSTest extends TestContext {
  @Test
  public void to_string() {
    var paramRefS = paramRefS(7, intTS(), "myName");
    assertThat(paramRefS.toString())
        .isEqualTo("""
            ParamRefS(
              evalT = Int
              paramName = myName
              location = myBuild.smooth:7
            )""");
  }
}
