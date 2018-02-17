package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.Location;

public class NamedNode extends Node implements Named {
  private final String name;

  public NamedNode(String name, Location location) {
    super(location);
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (!(NamedNode.class.equals(object.getClass()))) {
      return false;
    }
    NamedNode that = (NamedNode) object;
    return this.name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
