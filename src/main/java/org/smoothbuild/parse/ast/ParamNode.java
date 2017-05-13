package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.Type;

public class ParamNode extends Node {
  private final TypeNode typeNode;
  private final String name;
  private final Type type;

  public ParamNode(TypeNode typeNode, String name, CodeLocation codeLocation) {
    this(typeNode, null, name, codeLocation);
  }

  public ParamNode(TypeNode typeNode, Type type, String name, CodeLocation codeLocation) {
    super(codeLocation);
    this.typeNode = typeNode;
    this.type = type;
    this.name = name;
  }

  public TypeNode typeNode() {
    return typeNode;
  }

  public Type type() {
    return type;
  }

  public String name() {
    return name;
  }

  public ParamNode withType(Type type) {
    return new ParamNode(typeNode, type, name, codeLocation());
  }
}
