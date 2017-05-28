package org.smoothbuild.parse;

import java.util.Set;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.parse.ast.FuncNode;

public class DependencyStackElem {
  private final FuncNode node;
  private Dependency missing;

  public DependencyStackElem(FuncNode node) {
    this.node = node;
  }

  public Name name() {
    return node.name();
  }

  public Set<Dependency> dependencies() {
    return node.dependencies();
  }

  public Dependency missing() {
    return missing;
  }

  public void setMissing(Dependency missing) {
    this.missing = missing;
  }
}
