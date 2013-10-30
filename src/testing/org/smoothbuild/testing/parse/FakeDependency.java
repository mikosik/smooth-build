package org.smoothbuild.testing.parse;

import java.util.Set;

import org.smoothbuild.parse.Dependency;
import org.smoothbuild.testing.message.FakeCodeLocation;

import com.google.common.collect.Sets;

public class FakeDependency {
  public static Set<Dependency> dependencies(String... names) {
    Set<Dependency> result = Sets.newHashSet();
    for (String name : names) {
      result.add(dependency(name));
    }
    return result;
  }

  public static Dependency dependency(String name) {
    return new Dependency(new FakeCodeLocation(), name);
  }
}
