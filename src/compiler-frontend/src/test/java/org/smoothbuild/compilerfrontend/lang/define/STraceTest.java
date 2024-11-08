package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bucket.base.Alias.alias;
import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sTrace;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

public class STraceTest {
  @Test
  void equals_and_hashCode() {
    var tester = new EqualsTester();
    tester.addEqualityGroup(new STrace(), new STrace());
    tester.addEqualityGroup(sTrace("name1", location(1), "name1", location(1)));
    tester.addEqualityGroup(sTrace("name1", location(2), "name1", location(2)));
    tester.addEqualityGroup(sTrace("name2", location(1), "name2", location(1)));
    tester.addEqualityGroup(sTrace("name2", location(2), "name2", location(2)));
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
