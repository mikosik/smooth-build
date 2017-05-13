package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.CodeLocation;

public class ParamNode extends Node {
  private final TypeNode typeNode;
  private final String name;

  public ParamNode(TypeNode type, String name, CodeLocation codeLocation) {
    super(codeLocation);
    this.typeNode = type;
    this.name = name;
  }

  public TypeNode typeNode() {
    return typeNode;
  }

  public String name() {
    return name;
  }
}
