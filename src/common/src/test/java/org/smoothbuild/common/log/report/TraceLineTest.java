package org.smoothbuild.common.log.report;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.dagger.CommonTestContext;

public class TraceLineTest extends CommonTestContext {
  @Test
  void depth_of_single_line_is_1() {
    var line = new TraceLine("a", location(alias()), null);
    assertThat(line.depth()).isEqualTo(1);
  }

  @Test
  void depth_of_two_line_chain_is_2() {
    var line1 = new TraceLine("a", location(alias()), null);
    var line2 = new TraceLine("b", location(alias()), line1);
    assertThat(line2.depth()).isEqualTo(2);
  }

  @Test
  void depth_of_three_line_chain_is_3() {
    var line1 = new TraceLine("a", location(alias()), null);
    var line2 = new TraceLine("b", location(alias()), line1);
    var line3 = new TraceLine("c", location(alias()), line2);
    assertThat(line3.depth()).isEqualTo(3);
  }
}
