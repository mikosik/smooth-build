package org.smoothbuild.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.parse.ast.Named;

public class Field implements Named {
  private final ConcreteType type;
  private final String name;
  private final Location location;

  public Field(ConcreteType type, String name, Location location) {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
    this.location = requireNonNull(location);
  }

  public ConcreteType type() {
    return type;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Location location() {
    return location;
  }
}
