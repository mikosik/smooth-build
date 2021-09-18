package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Named;

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

  public String q() {
    return "`" + name() + "`";
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof NamedNode that
        && this.name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
