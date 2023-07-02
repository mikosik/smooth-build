package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.TestContext.importedFilePath;
import static org.smoothbuild.testing.TestContext.location;
import static org.smoothbuild.testing.TestContext.smoothFilePath;
import static org.smoothbuild.testing.TestContext.traceS;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class TraceSTest {
  @Test
  public void equals_and_hashCode() {
    var tester = new EqualsTester();
    tester.addEqualityGroup(new TraceS(), new TraceS());
    tester.addEqualityGroup(traceS("name1", location(1), "name1", location(1)));
    tester.addEqualityGroup(traceS("name1", location(2), "name1", location(2)));
    tester.addEqualityGroup(traceS("name2", location(1), "name2", location(1)));
    tester.addEqualityGroup(traceS("name2", location(2), "name2", location(2)));
    tester.testEquals();
  }

  @Test
  public void to_string() {
    var trace = traceS("first-name", location(smoothFilePath(), 17),
        "second-name", location(importedFilePath(), 19));
    assertThat(trace.toString())
        .isEqualTo("""
            @ myBuild.smooth:17  first-name
            @ imported.smooth:19 second-name""");
  }
}
