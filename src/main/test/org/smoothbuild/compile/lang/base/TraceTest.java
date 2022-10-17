package org.smoothbuild.compile.lang.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class TraceTest {
  @Test
  public void to_string() {
    var trace = new Trace<>("elem1", new Trace<>("elem2"));
    assertThat(trace.toString())
        .isEqualTo("""
            elem1
            elem2""");
  }
}
