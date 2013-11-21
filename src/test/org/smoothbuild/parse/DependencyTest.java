package org.smoothbuild.parse;

import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;

import org.junit.Test;

import com.google.common.testing.EqualsTester;

public class DependencyTest {

  @Test
  public void testEqualsAndHashCode() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(dependency(11, "equal"), dependency(11, "equal"));

    tester.addEqualityGroup(dependency(1, "a"), dependency(2, "a"), dependency(3, "a"));
    tester.addEqualityGroup(dependency(1, "b"), dependency(2, "b"), dependency(3, "b"));
    tester.addEqualityGroup(dependency(1, "ab"), dependency(2, "ab"), dependency(3, "ab"));
    tester.addEqualityGroup(dependency(1, "ba"), dependency(2, "ba"), dependency(3, "ba"));

    tester.testEquals();
  }

  private static Dependency dependency(int line, String name) {
    return new Dependency(codeLocation(line), name(name));
  }
}
