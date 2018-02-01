package org.smoothbuild.parse.deps;

import java.util.Set;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.parse.ast.Named;

public class StackElem {
  private final Name name;
  private Named missing;
  private final Set<Named> dependencies;

  public StackElem(Name name, Set<Named> dependencies) {
    this.name = name;
    this.dependencies = dependencies;
  }

  public Name name() {
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
