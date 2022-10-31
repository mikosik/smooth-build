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

  @Test
  public void to_string_with_null_name() {
    var trace = new TraceS(null, loc(17), new TraceS("second-name", loc(19)));
    assertThat(trace.toString())
        .isEqualTo("""
                        myBuild.smooth:17
            second-name myBuild.smooth:19""");
  }
}
