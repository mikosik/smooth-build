package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.Location;

public class TypeNode extends Node {
  private final String name;

  public TypeNode(String name, Location location) {
    super(location);
    this.name = name;
  }

  public String name() {
    return name;
  }
}
