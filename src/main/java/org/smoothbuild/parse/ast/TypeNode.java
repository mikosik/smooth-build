package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.CodeLocation;

public class TypeNode extends Node {
  private final String name;

  public TypeNode(String name, CodeLocation codeLocation) {
    super(codeLocation);
    this.name = name;
  }

  public String name() {
    return name;
  }
}
