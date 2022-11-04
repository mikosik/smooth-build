package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class DefValSTest extends TestContext {
  @Test
  public void to_string() {
    var annValS = defValS(7, stringTS(), "myVal", intS(9));
    assertThat(annValS.toString())
        .isEqualTo("""
            DefVal(
              type = String
              name = myVal
              loc = myBuild.smooth:7
              body = IntS(Int, 9, myBuild.smooth:1)
            )""");
  }
}
