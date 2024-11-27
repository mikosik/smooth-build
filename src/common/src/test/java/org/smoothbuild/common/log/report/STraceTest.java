package org.smoothbuild.common.log.report;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;
import static org.smoothbuild.common.filesystem.base.Path.path;

import com.google.common.testing.EqualsTester;
import com.google.common.truth.Truth;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.testing.CommonTestContext;

public class STraceTest extends CommonTestContext {
  @Test
  void equals_and_hashCode() {
    var tester = new EqualsTester();
    tester.addEqualityGroup(new STrace(), new STrace());
    var path = fullPath(alias(), "path");
    var location1 = location(path, 1);
    var location2 = location(path, 2);
    tester.addEqualityGroup(sTrace("name1", location1, "name1", location1));
    tester.addEqualityGroup(sTrace("name1", location2, "name1", location2));
    tester.addEqualityGroup(sTrace("name2", location1, "name2", location1));
    tester.addEqualityGroup(sTrace("name2", location2, "name2", location2));
    tester.testEquals();
  }

  @Test
  void to_string() {
    var trace = sTrace(
        "first-name",
        location(fullPath(alias("project"), path("build.smooth")), 17),
        "second-name",
        location(fullPath(alias("library"), path("imported.smooth")), 19));
    assertThat(trace.toString())
        .isEqualTo(
            """
            @ {project}/build.smooth:17 first-name
            @ {library}/imported.smooth:19 second-name""");
  }
}
