package org.smoothbuild.parse;

import java.util.Set;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.parse.ast.FuncNode;

public class DependencyStackElem {
  private final FuncNode node;
  private Dependency missing;
  private final Set<Dependency> dependencies;

  public DependencyStackElem(FuncNode node, Set<Dependency> dependencies) {
    this.node = node;
    this.dependencies = dependencies;
  }

  public Name name() {
    return node.name();
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
