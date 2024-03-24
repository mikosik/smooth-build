package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.importedBuildFullPath;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.userModuleFullPath;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;

public class STraceTest {
  @Test
  public void equals_and_hashCode() {
    var tester = new EqualsTester();
    tester.addEqualityGroup(new STrace(), new STrace());
    tester.addEqualityGroup(TestingSExpression.sTrace("name1", location(1), "name1", location(1)));
    tester.addEqualityGroup(TestingSExpression.sTrace("name1", location(2), "name1", location(2)));
    tester.addEqualityGroup(TestingSExpression.sTrace("name2", location(1), "name2", location(1)));
    tester.addEqualityGroup(TestingSExpression.sTrace("name2", location(2), "name2", location(2)));
    tester.testEquals();
  }

  @Test
  public void to_string() {
    var trace = TestingSExpression.sTrace(
        "first-name",
        location(userModuleFullPath(), 17),
        "second-name",
        location(importedBuildFullPath(), 19));
    assertThat(trace.toString())
        .isEqualTo(
            """
            @ {prj}/build.smooth:17    first-name
            @ {ssl}/imported.smooth:19 second-name""");
  }
}
