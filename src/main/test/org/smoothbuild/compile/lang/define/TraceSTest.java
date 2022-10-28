package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.TestContext.loc;

import org.junit.jupiter.api.Test;

public class TraceSTest {
  @Test
  public void to_string() {
    var trace = new TraceS("first-short", loc(17), new TraceS("second-very-long", loc(19)));
    assertThat(trace.toString())
        .isEqualTo("""
            first-short      myBuild.smooth:17
            second-very-long myBuild.smooth:19""");
  }
}
