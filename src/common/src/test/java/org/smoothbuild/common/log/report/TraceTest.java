package org.smoothbuild.common.log.report;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.testing.CommonTestContext;

public class TraceTest extends CommonTestContext {
  @Test
  void equals_and_hashCode() {
    var tester = new EqualsTester();
    tester.addEqualityGroup(trace(null), trace(null));
    tester.addEqualityGroup(trace(line("a", null)), trace(line("a", null)));
    tester.addEqualityGroup(trace(line("b", null)), trace(line("b", null)));
    tester.addEqualityGroup(trace(line("a", line("b", null))), trace(line("a", line("b", null))));
    tester.addEqualityGroup(trace(line("b", line("a", null))), trace(line("b", line("a", null))));
    tester.testEquals();
  }

  @Nested
  class _to_string {
    @Test
    void with_0_trace_points() {
      var trace = trace(null);
      assertThat(trace.toString()).isEqualTo("@ <empty trace>");
    }

    @Test
    void with_1_trace_points() {
      var trace = trace(line("first", null));
      assertThat(trace.toString()).isEqualTo("""
              @ {t-alias}/path:17 first""");
    }

    @Test
    void with_3_trace_points() {
      var trace = trace(line("first", line("second", line("third", null))));
      assertThat(trace.toString())
          .isEqualTo(
              """
              @ {t-alias}/path:17 first
              @ {t-alias}/path:17 second
              @ {t-alias}/path:17 third""");
    }
  }

  public static Trace trace(TraceLine topLine) {
    return new Trace(topLine);
  }

  private TraceLine line(String name, TraceLine next) {
    return new TraceLine(name, location(alias()), next);
  }
}
