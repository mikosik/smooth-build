package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class AnnValueSTest extends TestContext {
  @Test
  public void to_string() {
    var annS = new AnnS("myAnnotation", stringS(7, "myPath"), loc(17));
    var annValS = annValS(7, annS,  stringTS(), "myVal");
    assertThat(annValS.toString())
        .isEqualTo("""
            AnnVal(
              AnnS(
                name = myAnnotation
                path = StringS(String, "myPath", myBuild.smooth:7)
                loc = myBuild.smooth:17
              )
              type = String
              name = myVal
              loc = myBuild.smooth:7
            )""");
  }
}
