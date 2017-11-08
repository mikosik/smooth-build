package org.smoothbuild.parse.deps;

import static org.smoothbuild.lang.message.Location.location;

import java.nio.file.Paths;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Name;

import com.google.common.testing.EqualsTester;

public class DependencyTest {

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(dependency(11, "equal"), dependency(11, "equal"));

    tester.addEqualityGroup(dependency(1, "a"), dependency(2, "a"), dependency(3, "a"));
    tester.addEqualityGroup(dependency(1, "b"), dependency(2, "b"), dependency(3, "b"));
    tester.addEqualityGroup(dependency(1, "ab"), dependency(2, "ab"), dependency(3, "ab"));
    tester.addEqualityGroup(dependency(1, "ba"), dependency(2, "ba"), dependency(3, "ba"));

    tester.testEquals();
  }

  private static Dependency dependency(int line, String name) {
    return new Dependency(location(Paths.get("script.smooth"), line), new Name(name));
  }
}
