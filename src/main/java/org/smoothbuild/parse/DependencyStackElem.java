package org.smoothbuild.parse;

import java.util.Set;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.parse.ast.FunctionNode;

public class DependencyStackElem {
  private final FunctionNode node;
  private Dependency missing;

  public DependencyStackElem(FunctionNode node) {
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
