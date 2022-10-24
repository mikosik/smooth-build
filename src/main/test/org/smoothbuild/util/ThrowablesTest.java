package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Throwables.stackTraceToString;

import org.junit.jupiter.api.Test;

public class ThrowablesTest {
  @Test
  public void stack_trace_to_string() {
    var expected = """
        java.lang.RuntimeException: buga
        \tat org.smoothbuild.util.ThrowablesTest.stack_trace_to_string(ThrowablesTest.java:14)""";
    assertThat(stackTraceToString(new RuntimeException("buga")))
        .startsWith(expected);
  }
}
