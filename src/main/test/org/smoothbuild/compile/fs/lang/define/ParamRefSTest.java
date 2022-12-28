package org.smoothbuild.compile.fs.lang.define;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

import com.google.common.truth.Truth;

public class ParamRefSTest extends TestContext {
  @Test
  public void to_string() {
    var paramRefS = paramRefS(7, intTS(), "myName");
    Truth.assertThat(paramRefS.toString())
        .isEqualTo("""
            ParamRefS(
              evalT = Int
              paramName = myName
              location = myBuild.smooth:7
            )""");
  }
}
