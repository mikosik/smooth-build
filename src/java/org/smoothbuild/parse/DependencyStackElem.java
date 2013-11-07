package org.smoothbuild.parse;

import java.util.Set;

import org.smoothbuild.function.base.Name;

public class DependencyStackElem {
  private final Name name;
  private final Set<Dependency> dependencies;
  private Dependency missing;

  public DependencyStackElem(Name name, Set<Dependency> dependencies) {
    this.name = name;
    this.dependencies = dependencies;
  }

  public Name name() {
    return name;
  }

  public Set<Dependency> dependencies() {
    return dependencies;
  }

  public Dependency missing() {
    return missing;
  }

  public void setMissing(Dependency missing) {
    this.missing = missing;
  }
}
