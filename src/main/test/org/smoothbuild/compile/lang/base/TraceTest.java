package org.smoothbuild.compile.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.TestContext.loc;

import org.junit.jupiter.api.Test;

public class TraceTest {
  @Test
  public void to_string() {
    var trace = new Trace("myFunction()", loc(17),
        new Trace("otherFunction()", loc(27)));
    assertThat(trace.toString())
        .isEqualTo("""
            myFunction() myBuild.smooth:17
            otherFunction() myBuild.smooth:27""");
  }
}
