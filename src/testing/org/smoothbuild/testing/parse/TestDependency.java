package org.smoothbuild.testing.parse;

import static org.smoothbuild.message.CodeLocation.codeLocation;

import java.util.Set;

import org.smoothbuild.parse.Dependency;

import com.google.common.collect.Sets;

public class TestDependency {
  public static Set<Dependency> dependencies(String... names) {
    Set<Dependency> result = Sets.newHashSet();
    for (String name : names) {
      result.add(dependency(name));
    }
    return result;
  }

  public static Dependency dependency(String name) {
    return new Dependency(codeLocation(1, 2, 3), name);
  }
}
