package org.smoothbuild.parse.deps;

import java.util.Set;

import org.smoothbuild.parse.ast.Named;

public class StackElem {
  private final String name;
  private Named missing;
  private final Set<Named> dependencies;

  public StackElem(String name, Set<Named> dependencies) {
    this.name = name;
    this.dependencies = dependencies;
  }

  public String name() {
    return name;
  }

  public Set<Named> dependencies() {
    return dependencies;
  }

  public Named missing() {
    return missing;
  }

  public void setMissing(Named missing) {
    this.missing = missing;
  }
}
