package org.smoothbuild.parse.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.problem.CodeLocation;

public class Argument {
  private final String name;
  private final DefinitionNode node;
  private final CodeLocation codeLocation;

  public static Argument explicitArg(String name, DefinitionNode node, CodeLocation codeLocation) {
    return new Argument(name, node, codeLocation);
  }

  public static Argument implicitArg(DefinitionNode node, CodeLocation codeLocation) {
    return new Argument(null, node, codeLocation);
  }

  private Argument(String name, DefinitionNode node, CodeLocation codeLocation) {
    this.name = name;
    this.node = checkNotNull(node);
    this.codeLocation = checkNotNull(codeLocation);
  }

  public String name() {
    return name;
  }

  public DefinitionNode definitionNode() {
    return node;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }

  public boolean isExplicit() {
    return name != null;
  }
}
