package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.TestContext.location;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class TraceSTest {
  @Test
  public void equals_and_hashCode() {
    var tester = new EqualsTester();
    tester.addEqualityGroup(new TraceS(), new TraceS());
    tester.addEqualityGroup(new TraceS("name1", location(1)), new TraceS("name1", location(1)));
    tester.addEqualityGroup(new TraceS("name1", location(2)), new TraceS("name1", location(2)));
    tester.addEqualityGroup(new TraceS("name2", location(1)), new TraceS("name2", location(1)));
    tester.addEqualityGroup(new TraceS("name2", location(2)), new TraceS("name2", location(2)));
    tester.testEquals();
  }

  @Test
  public void to_string() {
    var trace = new TraceS(
        "first-short", location(17), new TraceS("second-very-long", location(19)));
    assertThat(trace.toString())
        .isEqualTo("""
            @ first-short      myBuild.smooth:17
            @ second-very-long myBuild.smooth:19""");
  }

  @Test
  public void to_string_with_null_name() {
    var trace = new TraceS(null, location(17), new TraceS("second-name", location(19)));
    assertThat(trace.toString())
        .isEqualTo("""
            @             myBuild.smooth:17
            @ second-name myBuild.smooth:19""");
  }
}
