package org.smoothbuild.parse;

import java.util.Set;

public class DependencyStackElem {
  private final String name;
  private final Set<Dependency> dependencies;
  private Dependency missing;

  public DependencyStackElem(String name, Set<Dependency> dependencies) {
    this.name = name;
    this.dependencies = dependencies;
  }

  public String name() {
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
