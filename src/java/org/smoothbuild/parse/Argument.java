package org.smoothbuild.parse;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.problem.SourceLocation;

public class Argument {
  private final String name;
  private final DefinitionNode node;
  private final SourceLocation sourceLocation;

  public Argument(String name, DefinitionNode node, SourceLocation sourceLocation) {
    this.name = name;
    this.node = checkNotNull(node);
    this.sourceLocation = checkNotNull(sourceLocation);
  }

  public String name() {
    return name;
  }

  public DefinitionNode definitionNode() {
    return node;
  }

  public SourceLocation sourceLocation() {
    return sourceLocation;
  }

  public boolean isExplicit() {
    return name != null;
  }
}
