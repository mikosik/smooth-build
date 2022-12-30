package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class EvaluableRefSTest extends TestContext {
  @Test
  public void to_string() {
    var evaluableRef = new EvaluableRefS(schemaS(intTS()), "referenced", location(7));
    assertThat(evaluableRef.toString())
        .isEqualTo("""
            EvaluableRefS(
              schema = <>Int
              name = referenced
              location = myBuild.smooth:7
            )""");
  }
}
