package org.smoothbuild.parse.deps;

import java.util.Set;

import org.smoothbuild.parse.ast.Named;

public class StackElem<T extends Named> {
  private final T named;
  private Named missing;
  private final Set<Named> dependencies;

  public StackElem(T named, Set<Named> dependencies) {
    this.named = named;
    this.dependencies = dependencies;
  }

  public T named() {
    return named;
  }

  public String name() {
    return named.name();
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
