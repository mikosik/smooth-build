package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.CodeLocation;

public class ParamNode extends Node {
  private final String type;
  private final String name;

  public ParamNode(String type, String name, CodeLocation codeLocation) {
    super(codeLocation);
    this.type = type;
    this.name = name;
  }

  public String type() {
    return type;
  }

  public String name() {
    return name;
  }
}
