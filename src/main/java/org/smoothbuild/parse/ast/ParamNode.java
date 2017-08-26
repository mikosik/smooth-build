package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;

public class ParamNode extends Node {
  private final TypeNode type;
  private final Name name;

  public ParamNode(TypeNode type, Name name, CodeLocation codeLocation) {
    super(codeLocation);
    this.type = type;
    this.name = name;
  }

  public TypeNode type() {
    return type;
  }

  public Name name() {
    return name;
  }
}
