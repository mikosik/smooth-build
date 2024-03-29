package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class ReferenceSTest extends TestContext {
  @Test
  public void to_string() {
    var refS = new ReferenceS(schemaS(intTS()), "referenced", location(7));
    assertThat(refS.toString())
        .isEqualTo("""
            ReferenceS(
              schema = <>Int
              name = referenced
              location = myBuild.smooth:7
            )""");
  }
}
