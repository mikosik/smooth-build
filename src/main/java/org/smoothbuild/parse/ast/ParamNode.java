package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.CodeLocation;

public class ParamNode extends Node {
  private final TypeNode type;
  private final String name;

  public ParamNode(TypeNode type, String name, CodeLocation codeLocation) {
    super(codeLocation);
    this.type = type;
    this.name = name;
  }

  public TypeNode type() {
    return type;
  }

  public String name() {
    return name;
  }
}
