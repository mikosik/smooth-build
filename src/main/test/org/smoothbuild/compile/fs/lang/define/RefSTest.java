package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class RefSTest extends TestContext {
  @Test
  public void to_string() {
    var refS = new RefS(schemaS(intTS()), "referenced", location(7));
    assertThat(refS.toString())
        .isEqualTo("""
            RefS(
              schema = <>Int
              name = referenced
              location = myBuild.smooth:7
            )""");
  }
}
