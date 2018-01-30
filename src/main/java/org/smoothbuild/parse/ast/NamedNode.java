package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.Location;

public class NamedNode extends Node {
  private final Name name;

  public NamedNode(Name name, Location location) {
    super(location);
    this.name = name;
  }

  public Name name() {
    return name;
  }
}
