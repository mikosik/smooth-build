package org.smoothbuild.testing.parse;

import java.util.Set;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.parse.Dependency;
import org.smoothbuild.testing.message.FakeCodeLocation;

import com.google.common.collect.Sets;

public class FakeDependency {
  public static Set<Dependency> dependencies(Name... names) {
    Set<Dependency> result = Sets.newHashSet();
    for (Name name : names) {
      result.add(dependency(name));
    }
    return result;
  }

  public static Dependency dependency(Name name) {
    return new Dependency(new FakeCodeLocation(), name);
  }
}
